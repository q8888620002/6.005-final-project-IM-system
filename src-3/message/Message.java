package message;

/*
 *  It’s an interface that provides common attributes and methods among message ADT.
 *  Every Message will be converted to JSONstring format before it is through to client or server 
 */
public interface Message {
	
		public String toJSONString();
		public Object getType();
}
