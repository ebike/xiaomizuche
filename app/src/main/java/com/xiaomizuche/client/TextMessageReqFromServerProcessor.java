package com.xiaomizuche.client;


import com.xiaomizuche.bean.JCShortMessageInfo;

public class TextMessageReqFromServerProcessor implements IProcessor
{
	private static TextMessageReqFromServerProcessor _instance = null;
	
	public boolean init()
	{
		ProcessorManager.instance().registerProcessor(TextMessageReqFromServerProcessor.instance());
		return true;
	}
			
	public static TextMessageReqFromServerProcessor instance()
	{
		if (_instance == null)
		{
			_instance = new TextMessageReqFromServerProcessor();
		}
		return _instance;
	}
	
	@Override
	public int process(JCProtocol protocol, IJCClientListener listener)
	{
		if (protocol.getDataType() != EnumDataType.DATA_TextMessageReqFromServer)
		{
			return -1;
		}	
		
		TextMessageReqFromServer notify = (TextMessageReqFromServer)protocol;
		JCShortMessageInfo msg = new JCShortMessageInfo(notify.getDevId(), notify.getText());
		listener.onReceivedMessage(msg);
		
		JCClient.instance().sendCommonResponse(protocol.getProtocolSeq(), protocol.getProtocolId(), (byte)0);
		return 0;
	}

}
