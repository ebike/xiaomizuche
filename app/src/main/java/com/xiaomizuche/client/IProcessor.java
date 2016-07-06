package com.xiaomizuche.client;

public interface IProcessor
{
	//如果成功处理，返回0，否则返回其它值
	int process(JCProtocol protocol, IJCClientListener listener);
}
