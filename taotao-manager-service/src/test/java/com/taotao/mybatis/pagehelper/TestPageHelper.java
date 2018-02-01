package com.taotao.mybatis.pagehelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;

public class TestPageHelper {
	
	@Test
	public void testPageHelper() {
		// 初始化spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-dao.xml");

		// 从容器中获得mapper代理对象
		TbItemMapper itemMapper = applicationContext.getBean(TbItemMapper.class);

		// 执行查询
		TbItemExample example = new TbItemExample();

		// 分页处理
		PageHelper.startPage(1, 30);
		List<TbItem> lists = itemMapper.selectByExample(example);

		// 取分业务信息
		System.out.println("结果集中的记录数" + lists.size());
		PageInfo<TbItem> pageInfo = new PageInfo<>(lists);
		System.out.println("总记录数" + pageInfo.getTotal());
		System.out.println("总页数" + pageInfo.getPages());
	}

}
