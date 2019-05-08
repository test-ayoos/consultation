package com.bytatech.ayoos.client.alfresco_rest_api.api;

import org.springframework.cloud.openfeign.FeignClient;
import com.bytatech.ayoos.client.alfresco_rest_api.ClientConfiguration;

@FeignClient(name="${alfrescoRestApi.name:alfrescoRestApi}", url="${alfrescoRestApi.url}", configuration = ClientConfiguration.class)
public interface ProbesApiClient extends ProbesApi {
}