package com.bytatech.ayoos.client.alfresco_rest_api.api;

import org.springframework.cloud.openfeign.FeignClient;
import com.bytatech.ayoos.client.alfresco_rest_api.ClientConfiguration;

@FeignClient(name="${alfrescoRestApi.name:alfrescoRestApi}", url="${alfrescoRestApi.url:http://localhost/alfresco/api/-default-/public/alfresco/versions/1}", configuration = ClientConfiguration.class)
public interface FavoritesApiClient extends FavoritesApi {
}