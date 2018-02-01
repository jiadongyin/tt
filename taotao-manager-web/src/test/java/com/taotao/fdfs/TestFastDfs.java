package com.taotao.fdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

public class TestFastDfs {

	@Test
	public void testUpload() throws Exception {
		//1.创建一个配置文件fast_dfs.conf， 配置文件的内容就是指定TrackerServer的地址
		//2.加载配置文件
		ClientGlobal.init("G:/java学习资料/解压文件/电商/day03/代码/taotao/taotao-manager-web/src/main/resources/resource/fast_dfs.conf");
		//3.创建一个TrackerClient对象。
		TrackerClient trackerClient = new TrackerClient();
		//4.通过TrackerClient对象获得TrackerServer对象。
		TrackerServer trackerServer = trackerClient.getConnection();
		//5.创建一个StorageServer的引用。null就可以。
		StorageServer storageServer = null;
		//6.创建一个StorageClient对象。两个参数TrackerServer、StorageServer
		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
		//7.使用StorageClient对象上传文件。
		//参数1：文件名 参数2：文件扩展名，不包含“.” 参数3：文件的元数据
		String[] strings = storageClient.upload_file("G:/temp/picture/尹佳栋 (2).jpg", "jpg", null);
		for (String string : strings) {
			System.out.println(string);
		}
	}
	
	/*@Test
	public void testFastDfsClient() throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("C:/workspace-taotao/taotao-manager-web/src/main/resources/resource/fast_dfs.conf");
		String string = fastDFSClient.uploadFile("C:/Users/Public/Pictures/Sample Pictures/Penguins.jpg");
		System.out.println(string);
	}*/
}
