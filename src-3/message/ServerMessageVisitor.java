package message;

/*
 * Visitor interface that visit with ToServerMessage
 */
public interface ServerMessageVisitor<T> {
		public T visit(SignInAndOut s) ;
		public T visit(ConvOps s) ;
		public T visit(ChatToServer s) ;
}
