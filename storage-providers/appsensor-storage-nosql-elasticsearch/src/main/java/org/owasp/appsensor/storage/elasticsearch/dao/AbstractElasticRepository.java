package org.owasp.appsensor.storage.elasticsearch.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.owasp.appsensor.core.*;
import org.owasp.appsensor.core.criteria.SearchCriteria;
import org.owasp.appsensor.storage.elasticsearch.mapping.ElasticSearchJsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * This is the abstract super class of all elastic search repositories.
 * It contains the main logic to store and query objects in elastic search.
 *
 * @author Maik Jäkel(m.jaekel@xsite.de) http://www.xsite.de
 */
@Repository
public abstract class AbstractElasticRepository {

    @Value("${appsensor.elasticsearch.indexname:appsensor}")
    private String configuredIndexName;

    @Value("${appsensor.elasticsearch.rotatingindex:true}")
    private boolean rotatingIndex;

    private String actualIndexName;

    @Value("${appsensor.elasticsearch.clustername:elasticsearch}")
    private String clustername;

    @Value("${appsensor.elasticsearch.host:localhost}")
    private String host;

    @Value("${appsensor.elasticsearch.port:9300}")
    private int port;

    private Client client;

    private ObjectMapper objectMapper;

    private static final DateFormatter DATE_FORMATTER = new DateFormatter("yyyy.MM.dd");

    @PostConstruct
    private void initRepository() throws IOException {

        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", clustername).build();

        client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));

        objectMapper = new ElasticSearchJsonMapper();
        updateIndex();
    }


    @Scheduled(cron = "0 0 0 * * *") //execute with every start of a new day
    private void updateIndex() throws IOException {
        actualIndexName = configuredIndexName;
        if (rotatingIndex) {
            actualIndexName += "-" + DATE_FORMATTER.print(new Date(), Locale.ENGLISH);
        }

        XContentBuilder mapping = generateCommonElasticMappingForType(getElasticIndexType());

        if (!getClient().admin().indices().exists(new IndicesExistsRequest(actualIndexName)).actionGet().isExists()) {
            getClient().admin().indices().create(new CreateIndexRequest(actualIndexName)).actionGet();
        }


        getClient().admin().indices()
                .preparePutMapping(actualIndexName)
                .setType(getElasticIndexType())
                .setSource(mapping)
                .execute().actionGet();
    }

    protected String getIndexName() {
        return actualIndexName;
    }

    protected abstract String getElasticIndexType();


    protected XContentBuilder generateCommonElasticMappingForType(String type) throws IOException {
        // @formatter:off
        return jsonBuilder()
                .startObject()
                    .startObject(type)
                        .field("dynamic", "true")
                        .startArray("dynamic_templates")
                                .startObject()
                                    .startObject("base")
                                        .field("match_mapping_type", "string")
                                        .startObject("mapping")
                                            .field("index", "not_analyzed")
                                        .endObject()
                                    .endObject()
                                .endObject()
                        .endArray()
                        .startObject("properties")
                            .startObject("user")
                                .startObject("properties")
                                    .startObject("ipAddress")
                                        .startObject("properties")
                                            .startObject("geoLocation")
                                                .field("type", "geo_point")
                                            .endObject()
                                        .endObject()
                                    .endObject()
                                .endObject()
                            .endObject()
                            .startObject("detectionSystem")
                                .startObject("properties")
                                    .startObject("ipAddress")
                                        .startObject("properties")
                                            .startObject("geoLocation")
                                                .field("type", "geo_point")
                                            .endObject()
                                        .endObject()
                                    .endObject()
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
        // @formatter:on
    }

    protected Client getClient() {
        return client;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    protected QueryBuilder convertSearchCriteriaToQueryBuilder(SearchCriteria searchCriteria) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        DetectionPoint detectionPoint = searchCriteria.getDetectionPoint();

        if (detectionPoint != null) {
            if (StringUtils.isNotBlank(detectionPoint.getCategory())) {
                query = query.must(QueryBuilders.termQuery("detectionPoint.category", detectionPoint.getCategory()));
            }

            if (StringUtils.isNotBlank(detectionPoint.getLabel())) {
                query = query.must(QueryBuilders.termQuery("detectionPoint.label", detectionPoint.getLabel()));
            }
            Threshold threshold = detectionPoint.getThreshold();

            if (threshold != null) {
                int thresholdCount = threshold.getCount();
                if (thresholdCount > 0) {
                    query = query.must(QueryBuilders.termQuery("detectionPoint.threshold.thresholdCount", thresholdCount));
                }

                Interval interval = threshold.getInterval();
                if (interval != null) {
                    int duration = interval.getDuration();
                    if (duration > 0) {
                        query = query.must(QueryBuilders.termQuery("detectionPoint.threshold.interval.duration", duration));
                    }

                    if (StringUtils.isNotBlank(interval.getUnit())) {
                        query = query.must(QueryBuilders.termQuery("detectionPoint.threshold.interval.unit", interval.getUnit()));
                    }
                }
            }
        }

        Collection<String> detectionSystemIds = searchCriteria.getDetectionSystemIds();
        if (detectionSystemIds != null && detectionSystemIds.size() > 0) {
            query = query.must(QueryBuilders.termsQuery("detectionSystem.detectionSystemId", detectionSystemIds));
        }


        String earliest = searchCriteria.getEarliest();
        if (StringUtils.isNotEmpty(earliest)) {
            query = query.must(QueryBuilders.rangeQuery("timestamp").from(earliest).includeLower(true));
        }

        User user = searchCriteria.getUser();
        if (user != null) {
            String username = user.getUsername();
            if (StringUtils.isNotBlank(username)) {
                query = query.must(QueryBuilders.termQuery("user.username", username));
            }
        }


        return query;
    }


    protected <T extends IAppsensorEntity> List<T> findBySearchCriteria(SearchCriteria searchCriteria, Class<T> type) throws IOException {
        SearchResponse searchResponse = getClient().prepareSearch(getIndexName())
                .setTypes(getElasticIndexType())
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setQuery(convertSearchCriteriaToQueryBuilder(searchCriteria))
                .execute()
                .actionGet();

        SearchHits hits = searchResponse.getHits();

        List<T> resultList = new ArrayList<>((int) hits.totalHits());

        for (SearchHit hit : hits) {
            T searchResult = getObjectMapper().readValue(hit.getSourceAsString(), type);
            searchResult.setId(hit.getId());

            resultList.add(searchResult);
        }

        return resultList;

    }

    public void save(IAppsensorEntity entity) throws JsonProcessingException {
        String id = getClient().prepareIndex(getIndexName(), getElasticIndexType())
                .setSource(getObjectMapper().writeValueAsBytes(entity))
                .setRefresh(true)
                .setId(entity.getId())
                .execute()
                .actionGet().getId();

        entity.setId(id);

    }


    @PreDestroy
    public void shutDownRepository() {
        client.close();
    }

}
