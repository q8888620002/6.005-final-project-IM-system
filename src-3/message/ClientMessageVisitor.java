package message;

/*
 * visitor class of the To client message 
 */
public interface ClientMessageVisitor <T>{
		public  T visit(ChatToClient t);
		public  T visit(Hint t);
		public  T visit(ErrorMessage t);
		public  T visit(Userlsit t);
		
}
