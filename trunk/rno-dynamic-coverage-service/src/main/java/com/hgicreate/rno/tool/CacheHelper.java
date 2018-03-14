package com.hgicreate.rno.tool;

import java.io.IOException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
@ConfigurationProperties(prefix = "rno")
@Service("cacheHelper")
public class CacheHelper {

	/* 单例模式 */  
	private CacheHelper() {
	}
	
	private String memcacheServers;
	protected MemcachedClient memcached ;  
	private MemcachedClientBuilder builder;
	private boolean hasInit = false;

	public void setBuilder(MemcachedClientBuilder builder) {
		this.builder = builder;
	}


	public void setMemcacheServers(String memcacheServers) {
		this.memcacheServers = memcacheServers;
	}

	public MemcachedClient getMemcached() {
		if(!hasInit){
			hasInit = true;
			init();
		}
		return memcached;
	}

	public void setMemcached(MemcachedClient memcached) {
		this.memcached = memcached;
	}

	public void init() {  
		builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(memcacheServers));
		// 设置连接池大小，即客户端个数  
        builder.setConnectionPoolSize(50);  
        // 宕机报警  
        builder.setFailureMode(true);  
        // 使用二进制文件  
        builder.setCommandFactory(new BinaryCommandFactory()); 
        // 使用一致性哈希算法（Consistent Hash Strategy）  
        builder.setSessionLocator(new KetamaMemcachedSessionLocator());  
        // 使用序列化传输编码  
        builder.setTranscoder(new SerializingTranscoder());  
        // 进行数据压缩，大于1KB时进行压缩  
        builder.getTranscoder().setCompressionThreshold(1024);  
        try {
			memcached = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }  
}
