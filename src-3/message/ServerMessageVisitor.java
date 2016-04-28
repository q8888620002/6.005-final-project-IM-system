package message;

/*
 * Visitor interface that visit with ToServerMessage
 */
public interface ServerMessageVisitor<T> {
		public T visit(SignInAndOut s) throws ErrorTypeException;
		public T visit(ConvOps s) throws ErrorTypeException;
		public T visit(ChatToServer s) throws ErrorTypeException;
}
