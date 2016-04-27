package userInfo;
/*
 * 	It contains user input / output handler and name of the user
 * 	It's an immutable object
 * 
 *   Rep invariant:
 *   	InputHandler is final 
 *   	OutputHandler is final  
 *   
 */
public class UserInfo {
		private final InputHandler input;
		private final OutputHandler output;
		
		/**
		 * Create a UserInfo 
		 * @param input, InputHandler of the user
		 * @param out, OutputHandler of the user
		 */
		public UserInfo(InputHandler input, OutputHandler out){
			this.input = input;
			this.output = out;
		}
		
		
}
