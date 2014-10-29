package com.linda.framework.rpc.oio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.linda.framework.rpc.exception.RpcException;
import com.linda.framework.rpc.net.AbstractRpcAcceptor;

public class RpcOioAcceptor extends AbstractRpcAcceptor{
	
	private ServerSocket server;
	private List<RpcOioConnector> connectors;
	private RpcOioWriter writer;
	
	public RpcOioAcceptor(){
		super();
		connectors = new ArrayList<RpcOioConnector>();
		writer = new RpcOioWriter();
	}
	
	public void startService(){
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(host,port));
			this.startListeners();
			new AcceptThread().start();
		} catch (Exception e) {
			throw new RpcException(e);
		}
	}
	
	@Override
	public void stopService() {
		stop = true;
		for(RpcOioConnector connector:connectors){
			connector.stopService();
		}
		this.stopListeners();
	}

	private class AcceptThread extends Thread{
		@Override
		public void run() {
			while(!stop){
				try {
					Socket socket = server.accept();
					RpcOioConnector connector = new RpcOioConnector(socket,writer);
					RpcOioAcceptor.this.addConnectorListeners(connector);
					connector.startService();
				} catch (IOException e) {
					throw new RpcException(e);
				}
			}
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}