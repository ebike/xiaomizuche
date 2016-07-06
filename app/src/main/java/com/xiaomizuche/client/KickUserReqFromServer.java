package com.xiaomizuche.client;

public class KickUserReqFromServer extends JCProtocol
{
	private int _reason = 1;

	public KickUserReqFromServer()
	{
		super(EnumDataType.DATA_KickUserReqFromServer);
	}

	@Override
	public void decodeContent()
	{
		setReason(_content[0]);
	}

	public int getReason()
	{
		return _reason;
	}

	public void setReason(int reason)
	{
		_reason = reason;
	}
}
