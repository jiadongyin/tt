package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbUser;

/**
 * 订单确认页面controller
 * <p>
 * Title: OrderController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: www.itcast.cn
 * </p>
 * 
 * @version 1.0
 */
@Controller
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Value("${COOKIE_CART_KEY}")
	private String COOKIE_CART_KEY;
	@Value("${COOKIE_CART_EXPIRE}")
	private Integer COOKIE_CART_EXPIRE;

	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request,String checkedid) {
		// 取购物车商品列表
		List<TbItem> cartList = getCartList(request);
		
		//应该是选中用户勾选的商品，而不是所有商品
		String[] split = checkedid.split(":");
		//新建一个空的购物车列表
		List<TbItem> newCartList = new ArrayList<TbItem>();
		//将用户勾选的商品添加到新的购物车列表
		for (TbItem tbItem : cartList) {
			Long id = tbItem.getId();
			for(int i = 1;i <split.length;i++ ){
				if((split[i].equals(id+""))){
					System.out.println(split[i]+"===="+id);
					newCartList.add(tbItem);
				}
			}
		}
		
		//取用户id
		TbUser user = (TbUser) request.getAttribute("user");
		System.out.println(user.getUsername());
		//根据用户id查询收货地址列表。静态数据
		//从数据库中查询支付方式列表。
		//传递给页面
		request.setAttribute("cartList", newCartList);
		//返回逻辑视图
		return "order-cart";
	}

	// 从cookie中取购物车列表
	private List<TbItem> getCartList(HttpServletRequest request) {
		// 使用CookieUtils取购物车列表
		String json = CookieUtils.getCookieValue(request, COOKIE_CART_KEY, true);
		// 判断cookie中是否有值
		if (StringUtils.isBlank(json)) {
			// 没有值返回一个空List
			return new ArrayList<>();
		}
		// 把json转换成list对象
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;

	}
	
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, Model model, HttpServletRequest request,HttpServletResponse response) {
	    // 取用户id
	    TbUser user = (TbUser) request.getAttribute("user");
	    orderInfo.setUserId(user.getId());
	    orderInfo.setBuyerNick(user.getUsername());
	    // 调用服务生成订单
	    TaotaoResult result = orderService.createOrder(orderInfo);
	    // 取订单号并传递给页面
	    String orderId = result.getData().toString();
	    model.addAttribute("orderId", orderId);
	    model.addAttribute("payment", orderInfo.getPayment());
	    // 预计送达时间是三天后
	    DateTime dateTime = new DateTime();
	    dateTime = dateTime.plusDays(3);
	    model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
	   
	    //清除购物车中已付款提交的商品
	    //根据订单id查询商品信息
	    List<TbOrderItem> itemList = orderService.selectItemByOrderId(orderId);
	    //取购物车商品列表
	    List<TbItem> cartList = getCartList(request);
	    for (TbOrderItem tbOrderItem : itemList) {
	    	String itemId = tbOrderItem.getItemId();
	  		//找到对应的商品
	  		for (TbItem tbItem : cartList) {
	  			if (tbItem.getId().longValue() == Long.valueOf(itemId)) {
	  				//删除商品
	  				cartList.remove(tbItem);
	  				//退出循环
	  				break;
	  			}
	  		}
		}
	    //写入cookie
	    CookieUtils.setCookie(request, response, COOKIE_CART_KEY, 
	    JsonUtils.objectToJson(cartList), COOKIE_CART_EXPIRE, true);
	 
	    // 返回逻辑视图
	    return "success";
	}
}
