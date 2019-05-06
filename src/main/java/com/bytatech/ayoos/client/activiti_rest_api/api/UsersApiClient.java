package com.bytatech.ayoos.client.activiti_rest_api.api;

import org.springframework.cloud.openfeign.FeignClient;
import com.bytatech.ayoos.client.activiti_rest_api.ClientConfiguration;

@FeignClient(name="${activitiRestApi.name:activitiRestApi}", url="${activitiRestApi.url:http://35.237.193.86:8070/activiti-rest/service}", configuration = ClientConfiguration.class)
public interface UsersApiClient extends UsersApi {
}