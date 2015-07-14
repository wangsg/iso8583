package cn.agiledata.iso8583;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.util.ISO8583Socket;

/**
 * 河北一卡通测试
 * 
 * @author zln
 *
 */
public class TestHBCityCard extends TestBase {
	
	private static final Logger log = Logger.getLogger(TestHBCityCard.class);

	@Test
	// 签到
	public void signIn() throws Exception{
		try {
			SignRequest objSignReq = new SignRequest();
			
			String terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
			objSignReq.setTerminalNo("000" + "00103951");
			objSignReq.setTraceNo("0");
			objSignReq.setSeqNo(terminalTraceNo);
			objSignReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignReq.setOperator("000001");
			
			
//			objSignReq.setReserved63("000001"+"00000000000000000000000000" + "000000000" + "000000" + "0" + "00000000000000000000000000"
//			+ "0000000000000000");
			
			
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			byte[] request = message.getMessage();
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_CUPS, objSignReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objSignResp = new SignResponse();
			msgResponse.getResponseData(objSignResp);
			objSignResp.process();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
