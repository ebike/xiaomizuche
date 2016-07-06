package com.xiaomizuche.client;


import com.xiaomizuche.map.JCLocationManager;

public class QueryLocationReqFromServerProcessor implements IProcessor
{
	private static QueryLocationReqFromServerProcessor _instance = null;
	
	public boolean init()
	{
		ProcessorManager.instance().registerProcessor(QueryLocationReqFromServerProcessor.instance());
		return true;
	}
			
	public static QueryLocationReqFromServerProcessor instance()
	{
		if (_instance == null)
		{
			_instance = new QueryLocationReqFromServerProcessor();
		}
		return _instance;
	}
	
	@Override
	public int process(JCProtocol protocol, IJCClientListener listener)
	{
		if (protocol.getDataType() != EnumDataType.DATA_QueryLocationReqFromServer)
		{
			return -1;
		}
		
		//需要上报位置信息
		JCLocationManager.instance().uploadLastKnownLocation();
		return 0;
	}

}
