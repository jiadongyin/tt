package com.taotao.order.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbOrderItem;

public interface OrderService {

	TaotaoResult createOrder(OrderInfo orderInfo);

	List<TbOrderItem> selectItemByOrderId(String orderId);
		
	
}
