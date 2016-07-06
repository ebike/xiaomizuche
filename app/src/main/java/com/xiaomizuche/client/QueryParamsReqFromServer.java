package com.xiaomizuche.client;

public class QueryParamsReqFromServer extends JCProtocol
{

	public QueryParamsReqFromServer()
	{
		super(EnumDataType.DATA_QueryParamsReqFromServer);
	}

	@Override
	public void decodeContent()
	{
		//消息体为空
	}

}
