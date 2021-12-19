package com.mgumieniak.architecture.connectors;

import reactivefeign.spring.config.ReactiveFeignClient;

import static com.mgumieniak.architecture.connectors.Paths.Order.ORDERS;

@ReactiveFeignClient(value = "order-client", path = ORDERS, url = "${feign.clients.order}")
public interface OrderClient extends OrderClientContract {


}
