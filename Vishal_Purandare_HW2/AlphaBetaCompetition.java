import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.TreeSet;

/**
 * Mini Max with Alpha Beta implementation of the Othello (Reversi)
 * @author Vishal Purandare
 * 
 */
public class AlphaBetaCompetition {
	private InputBean inBean;
	NavigableSet<MoveState> stateTreeSet;
	private int depth;
	private Queue<MoveState> stateQueue;
	private boolean passMax = false;
	private boolean passMin = false;

	static char[] colAlphabates = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	private char blankSymbol;
	private char anchorChar;
	private boolean ifMiniMax;
	
	public AlphaBetaCompetition(InputBean inBean) {
		this.inBean = inBean;
		this.stateTreeSet = new TreeSet<MoveState>(new MoveState());
		this.depth = 0;
		this.stateQueue = new LinkedList<MoveState>();
		this.blankSymbol = '*';
		this.anchorChar = 'a';
		this.ifMiniMax = false;
	}
	public void runRunAlphaBeta(){
		try {
			int alpha = -Integer.MAX_VALUE;
			int beta = Integer.MAX_VALUE;
			int finalMax = 0;		
			String move = "";
			
			finalMax = maxValue(this.inBean.getGraphArr(), this.inBean.getCuttingOffDepth(), "root", this.inBean.getMyPlayerSymbol(), this.inBean.getOppoPlayerSymbol(), depth, alpha, beta);
			while (!stateQueue.isEmpty()) {
				MoveState ms = stateQueue.remove();
				if(ms.getScore() == finalMax) {
					move = ms.getEndPosition();
					break;
				}
			}
			if(!move.equals("")) {
				trace(move);
			}
			else {
				trace("pass");
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int maxValue(char[][] positionArray, int max_depth,
			String endPos, char myPlayerSymbol, char oppoPlayerSymbol, int depth, int alpha, int beta) throws Exception {
		if(depth == max_depth) {
			int score = calculateEvalForChangedBoard(myPlayerSymbol, oppoPlayerSymbol, positionArray);
			return score;
		}
		else {
			 int score = - Integer.MAX_VALUE;
			 stateTreeSet = new TreeSet<MoveState>(new MoveState());
			 for(int i = 0; i < positionArray.length; i++) {
					for (int j = 0; j < positionArray.length; j++) {
						if(positionArray[i][j] == myPlayerSymbol) {
							stateTreeSet = findValidPlacings(i, j, positionArray, stateTreeSet, oppoPlayerSymbol, myPlayerSymbol);
						}
					}
				}
				List<MoveState> moveStateList = new ArrayList<MoveState>();
				if(stateTreeSet.isEmpty()) {
					passMax = true;
					if(passMin) {
						score = calculateEvalForChangedBoard(myPlayerSymbol, oppoPlayerSymbol, positionArray);
						if(alpha < score) {
							if(score >= beta) {
								return beta;
							}
							alpha = score;
	 					}
						return score;
					}
					int value  = minValue(positionArray, max_depth, "pass", oppoPlayerSymbol, myPlayerSymbol, depth+1, alpha, beta);
					if(value > score) {
						score = value;
					}
					if(alpha < score) {
						if(value >= beta) {
							return beta;
						}
						alpha = score;
 					}
					return score;
				}
				else {
					passMax = false;
					MoveState moveState;
					while (!stateTreeSet.isEmpty()) {
						boolean flag = true;
						moveState = stateTreeSet.pollFirst();
						if(!moveStateList.isEmpty()) {
							for (MoveState mvSt : moveStateList) {
								if(mvSt.getEndPosition().equals(moveState.getEndPosition())) {
									flag = false;
								}
							}
						}
						if(flag) {
							moveStateList.add(moveState);
						}
					}
					
					for (MoveState mState : moveStateList) {
						char[][] positionArrayTemp = mState.getChangedPositionArray() ;
						int value = minValue(positionArrayTemp, max_depth, mState.getEndPosition(), oppoPlayerSymbol, myPlayerSymbol, (depth+1), alpha, beta);
						if(value > score) {
							score = value;
						}
						if(alpha < score) {
							if(value >= beta) {
								return beta;
							}
							alpha = score;
 						}
						if(endPos.equals("root")) {
							mState.setScore(score);
							stateQueue.add(mState);
						}
					}
					if(endPos.equals("pass")) {
						return score;
					}
					else {
						return alpha;
					}
				}
		}
	}
	
	private int minValue(char[][] positionArray, int max_depth,
			String endPos, char myPlayerSymbol, char oppoPlayerSymbol, int depth, int alpha, int beta) throws Exception {
		if(depth == max_depth) {
			int score = calculateEvalForChangedBoard(myPlayerSymbol, oppoPlayerSymbol, positionArray);
			return score;
		 }
		else {
			 int score =  Integer.MAX_VALUE;
			 stateTreeSet = new TreeSet<MoveState>(new MoveState());
			 for (int i = 0; i < positionArray.length; i++) {
					for (int j = 0; j < positionArray.length; j++) {
						if(positionArray[i][j] == myPlayerSymbol) {
							stateTreeSet = findValidPlacings(i, j, positionArray, stateTreeSet, oppoPlayerSymbol, myPlayerSymbol);
						}
					}
				}
				List<MoveState> moveStateList = new ArrayList<MoveState>();
				if(stateTreeSet.isEmpty()) {
					passMin = true;
					if(passMax){
						score = calculateEvalForChangedBoard(myPlayerSymbol, oppoPlayerSymbol, positionArray);
						if(beta > score) {
							if(score <= alpha) {
								return alpha;
							}
							beta = score;
	 					}
						return score;
					}
					int value = maxValue(positionArray, max_depth, "pass", oppoPlayerSymbol, myPlayerSymbol, depth+1, alpha, beta);
					if(value < score) {
						score = value;
					}
					if(beta > score) {
						if(value <= alpha) {
							return alpha;
						}
						beta = score;
 					}
					return score;
				}
				else {
					passMin = false;
					MoveState moveState;
					while (!stateTreeSet.isEmpty()) {
						boolean flag = true;
						moveState = stateTreeSet.pollFirst();
						if(!moveStateList.isEmpty()) {
							for (MoveState mvSt : moveStateList) {
								if(mvSt.getEndPosition().equals(moveState.getEndPosition())) {
									flag = false;
								}
							}
						}
						if(flag) {
							moveStateList.add(moveState);
						}
					}
					
					for (MoveState mState : moveStateList) {
						char[][] positionArrayTemp = mState.getChangedPositionArray() ;
						int value = maxValue(positionArrayTemp, max_depth, mState.getEndPosition(), oppoPlayerSymbol, myPlayerSymbol, (depth+1), alpha, beta);
						if(value < score) {
							score = value;
						}
						if(beta > score) {
							if(value <= alpha) {
								return alpha;
							}
							beta = score;
 						}
					}
					if(endPos.equals("pass")) {
						return score;
					}
					else {
						return beta;
					}
				}
		}
	}
	
	private void trace(String str) {
		File file = new File("output.txt");
		FileWriter fw = null;
		BufferedWriter brWr = null;
		try {
			fw = new FileWriter(file);
			brWr = new BufferedWriter(fw);
			brWr.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				brWr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @param i
	 * @param j
	 * @param positionArray
	 * @param stateTreeSet
	 * @param oppoPlayerSymbol
	 * @param myPlayerSymbol
	 * @return NavigableSet<MoveState>
	 * 
	 */
	public NavigableSet<MoveState> findValidPlacings(int i, int j, char[][] positionArray, NavigableSet<MoveState> stateTreeSet, 
			char oppoPlayerSymbol, char myPlayerSymbol) {
		
		MoveState currentMoveState;
		boolean checkVerticallyUpward = false;//1
		boolean checkDiagonallyLeftUpward = false;//2
		boolean checkDiagonallyRightUpward = false;//3
		boolean checkVerticallyDownward = false;//4
		boolean checkDiagonallyLeftDownward= false;//5
		boolean checkDiagonallyRightDownward = false;//6
		boolean checkHorizontallyLeft = false;//7
		boolean checkHorizontallyRight = false;//8
		//1
		int verticalTopPostionRow = Integer.MAX_VALUE;
		int verticalTopPostionCol = Integer.MAX_VALUE;
		//2
		int diagonalTopLeftRow = Integer.MAX_VALUE;
		int diagonalTopLeftCol = Integer.MAX_VALUE;
		//3
		int diagonalTopRightRow = Integer.MAX_VALUE;
		int diagonalTopRightCol = Integer.MAX_VALUE;
		//4
		int verticalDownPostionRow = Integer.MAX_VALUE;
		int verticalDownPostionCol = Integer.MAX_VALUE;
		//5
		int diagonalDownLeftRow = Integer.MAX_VALUE;
		int diagonalDownLeftCol = Integer.MAX_VALUE;
		//6
		int diagonalDownRightRow = Integer.MAX_VALUE;
		int diagonalDownRightCol = Integer.MAX_VALUE;
		//7
		int horizontalLeftRow = Integer.MAX_VALUE;
		int horizontalLeftCol = Integer.MAX_VALUE;
		//8
		int horizontalRightRow = Integer.MAX_VALUE;
		int horizontalRightCol = Integer.MAX_VALUE;
		
		if(i != 0) {
			if(positionArray[i-1][j] == oppoPlayerSymbol) {//1
				checkVerticallyUpward= true;
			}
		}
		if(i != 0 && j !=0) {
			if(positionArray[i-1][j-1]  == oppoPlayerSymbol) {//2
				checkDiagonallyLeftUpward = true;
			}
		}
		if(i != 0 && j != positionArray.length-1) {
			if(positionArray[i-1][j+1]  == oppoPlayerSymbol) {//3
				checkDiagonallyRightUpward = true;
			}
		}
		if(i != positionArray.length-1) {
			if(positionArray[i+1][j] == oppoPlayerSymbol) {//4
				checkVerticallyDownward = true;
			}
		}
		if(i != positionArray.length-1 && j != 0) {
			if(positionArray[i+1][j-1] == oppoPlayerSymbol) {//5
				checkDiagonallyLeftDownward = true;
			}	
		}
		if(i != positionArray.length-1 && j != positionArray.length-1) {
			if(positionArray[i+1][j+1] == oppoPlayerSymbol) {//6
				checkDiagonallyRightDownward =  true;
			}
		}
		if(j != 0) {
			if(positionArray[i][j-1] == oppoPlayerSymbol) {//7
				checkHorizontallyLeft = true;
			}
		}
		if(j != positionArray.length-1) {
			if(positionArray[i][j+1] == oppoPlayerSymbol) {//8
				checkHorizontallyRight = true;
			}
		}
		
		//1
		if(checkVerticallyUpward) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = i-2; k >= 0; k--) {
				if(positionArray[k][j] == this.blankSymbol) {
					verticalTopPostionRow = k;
					verticalTopPostionCol = j;
					break;
				}
				else if (positionArray[k][j] == myPlayerSymbol) {
					break;
				}
			}
			if(verticalTopPostionRow == Integer.MAX_VALUE) {
				//System.out.println("No vertical top column position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + verticalTopPostionRow +", " + verticalTopPostionCol );				
				updatedPositionArray[verticalTopPostionRow][verticalTopPostionCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, verticalTopPostionRow, verticalTopPostionCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.VERTICALLY_UPWARD);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		//2
		if(checkDiagonallyLeftUpward) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = i-2, k2 = j-2 ; k >= 0; k--) {
				if(k2>=0) {
					if(positionArray[k][k2] == this.blankSymbol) {
						diagonalTopLeftRow = k;
						diagonalTopLeftCol = k2;
						break;
					}
					else if (positionArray[k][k2] == myPlayerSymbol) {
						break;
					}
					k2--;
				}
			}
			if(diagonalTopLeftRow == Integer.MAX_VALUE && diagonalTopLeftCol == Integer.MAX_VALUE) {
				//System.out.println("No Diagonal Left Upward position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + diagonalTopLeftRow +", " + diagonalTopLeftCol );
				updatedPositionArray[diagonalTopLeftRow][diagonalTopLeftCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, diagonalTopLeftRow, diagonalTopLeftCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.DIAGONALLY_LEFT_UPWARD);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		//3
		if(checkDiagonallyRightUpward) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = i-2, k2 = j+2; k >= 0; k--) {
				if(k2 < positionArray.length){
					if(positionArray[k][k2] == this.blankSymbol) {
						diagonalTopRightRow = k;
						diagonalTopRightCol = k2;
						break;
					}
					else if (positionArray[k][k2] == myPlayerSymbol) {
						break;
					}
					k2++;
				}
			}
			if(diagonalTopRightRow == Integer.MAX_VALUE && diagonalTopRightCol == Integer.MAX_VALUE) {
				//System.out.println("No Diagonal Right Upward position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + diagonalTopRightRow +", " + diagonalTopRightCol );
				updatedPositionArray[diagonalTopRightRow][diagonalTopRightCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, diagonalTopRightRow, diagonalTopRightCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.DIAGONALLY_RIGHT_UPWARD);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		//4
		if(checkVerticallyDownward) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = i+2; k < positionArray.length; k++) {
				if(positionArray[k][j] == this.blankSymbol) {
					verticalDownPostionRow = k;
					verticalDownPostionCol = j;
					break;
				}
				else if (positionArray[k][j] == myPlayerSymbol) {
					break;
				}
			}
			if(verticalDownPostionRow == Integer.MAX_VALUE) {
				//System.out.println("No vertical Down postion for player: " + myPlayerSymbol + "at " + i + ", " + j);
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + verticalDownPostionRow +", " + verticalDownPostionCol );
				updatedPositionArray[verticalDownPostionRow][verticalDownPostionCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, verticalDownPostionRow, verticalDownPostionCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.VERTICALLY_DOWNWARD);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		//5
		if(checkDiagonallyLeftDownward) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = i+2, k2 = j-2 ; k < positionArray.length; k++) {
				if(k2 >= 0){
					if(positionArray[k][k2] == this.blankSymbol) {
							diagonalDownLeftRow = k;
							diagonalDownLeftCol = k2;
							break;
					}
					else if (positionArray[k][k2] == myPlayerSymbol) {
						break;
					}
					k2--;
				}
			}
			if(diagonalDownLeftRow == Integer.MAX_VALUE && diagonalDownLeftCol == Integer.MAX_VALUE) {
				//System.out.println("No Diagonal Left Downward position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + diagonalDownLeftRow +", " + diagonalDownLeftCol );
				updatedPositionArray[diagonalDownLeftRow][diagonalDownLeftCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, diagonalDownLeftRow, diagonalDownLeftCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.DIAGONALLY_LEFT_DOWNWARD);
				stateTreeSet.add(currentMoveState);
			}
		}
				
		//6
		if(checkDiagonallyRightDownward) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = i+2, k2 = j+2; k < positionArray.length; k++) {
				if(k2 < positionArray.length) {
					if(positionArray[k][k2] == this.blankSymbol) {
						diagonalDownRightRow = k;
						diagonalDownRightCol = k2;
						break;
					}
					else if (positionArray[k][k2] == myPlayerSymbol) {
						break;
					}
					k2++;
				}
			}
			if(diagonalDownRightRow == Integer.MAX_VALUE && diagonalDownRightCol == Integer.MAX_VALUE) {
					//System.out.println("No Diagonal Right Downward position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + diagonalDownRightRow +", " + diagonalDownRightCol );
				updatedPositionArray[diagonalDownRightRow][diagonalDownRightCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, diagonalDownRightRow, diagonalDownRightCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.DIAGONALLY_RIGHT_DOWNWARD);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		//7
		if(checkHorizontallyLeft) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = j-2; k >= 0; k--) {
				if(positionArray[i][k] == this.blankSymbol) {
					horizontalLeftRow = i;
					horizontalLeftCol = k;
					break;
				}
				else if (positionArray[i][k] == myPlayerSymbol) {
					break;
				}
			}
			if(horizontalLeftCol == Integer.MAX_VALUE) {
				//System.out.println("No Horizontal left row position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + horizontalLeftRow +", " + horizontalLeftCol );
				updatedPositionArray[horizontalLeftRow][horizontalLeftCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, horizontalLeftRow, horizontalLeftCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.HORIZONTALLY_LEFT);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		//8
		if(checkHorizontallyRight) {
			char[][] updatedPositionArray = getNewUpdatedPostionArray(positionArray);
			for (int k = j+2; k < positionArray.length; k++) {
				if(positionArray[i][k] == this.blankSymbol) {
					horizontalRightRow = i;
					horizontalRightCol = k;
					break;
				}
				else if (positionArray[i][k] == myPlayerSymbol) {
					break;
				}
			}
			if(horizontalRightCol == Integer.MAX_VALUE) {
				//System.out.println("No Horizontal Right row position for player: " + myPlayerSymbol + "at " + i + ", " + j );
			}
			else {
				//System.out.println("Player " + myPlayerSymbol + " can be placed at " + horizontalRightRow +", " + horizontalRightCol );
				updatedPositionArray[horizontalRightRow][horizontalRightCol] = this.anchorChar;
				currentMoveState = getCurrentMoveState(updatedPositionArray, horizontalRightRow, horizontalRightCol, i, j, myPlayerSymbol, oppoPlayerSymbol, Constants.HORIZONTALLY_RIGHT);
				stateTreeSet.add(currentMoveState);
			}
		}
		
		return stateTreeSet;
	}

	/**
	 * @param updatedPositionArray
	 * @param row
	 * @param col
	 * @param i
	 * @param j
	 * @param myPlayerSymbol
	 * @param oppoPlayerSymbol
	 * @param moveType
	 * @return {@link MoveState}
	 */
	private MoveState getCurrentMoveState(char[][] updatedPositionArray, int row, int col, int i, int j, char myPlayerSymbol, char oppoPlayerSymbol, String moveType) {
		//int maxEvalValue = evalArray[row][col];
		String startPos = colAlphabates[j] + Integer.toString(i+1);
		String endPos = colAlphabates[col] + Integer.toString(row+1);
		MoveState currentMoveState = new MoveState();
		currentMoveState = updateCurrentMoveState(currentMoveState, i, j, row, col, updatedPositionArray, moveType, startPos, endPos, updatedPositionArray, 0);
		char[][] changedPostionArray = changeBoard(currentMoveState, updatedPositionArray, myPlayerSymbol, oppoPlayerSymbol);
		int maxEvalValue = 0;
		if(!this.ifMiniMax) {
			maxEvalValue = calculateEvalForChangedBoard(myPlayerSymbol, oppoPlayerSymbol, changedPostionArray);
		}
		
		currentMoveState = new MoveState(updatedPositionArray, maxEvalValue, moveType, i, j, row, col, startPos, endPos);
		currentMoveState = updateCurrentMoveState(currentMoveState, i, j, row, col, updatedPositionArray, moveType, startPos, endPos, changedPostionArray, maxEvalValue);
		
		return currentMoveState;
	}

	/**
	 * @param myPlayerSymbol
	 * @param oppoPlayerSymbol
	 * @param changedPostionArray
	 * @return (myEvalSum - oppoEvalSum)
	 */
	public int calculateEvalForChangedBoard(char myPlayerSymbol, char oppoPlayerSymbol, char[][] changedPostionArray) {
		int myCount = 0;
		int oppoCount = 0;
		
		for (int i = 0; i < changedPostionArray.length; i++) {
			for (int j = 0; j < changedPostionArray.length; j++) {
				if(changedPostionArray[i][j] == myPlayerSymbol) {
					myCount ++;
				}
				if(changedPostionArray[i][j] == oppoPlayerSymbol) {
					oppoCount ++;
				}
			}
		}
		return (myCount - oppoCount);
	}

	/**
	 * @param positionArray2
	 * @return updatedPositionArray
	 */
	private char[][] getNewUpdatedPostionArray(char[][] positionArray2) {
		char[][] updatedPositionArray = new char[8][8];
		
		for (int i=0; i <positionArray2.length; i++) {
	        for (int j = 0; j < positionArray2.length; j++) {
				updatedPositionArray[i][j] = positionArray2[i][j];
			}
	    }
		return updatedPositionArray;
	}
	
	/**
	 * @param currentMoveState
	 * @param startIndexRow
	 * @param startIndexCol
	 * @param endIndexRow
	 * @param endIndexCol
	 * @param updatedPositionArray
	 * @param moveType
	 * @param startPos
	 * @param endPos
	 * @param changedPositionArray
	 * @param maxEval
	 * @return {@link MoveState}
	 */
	private MoveState updateCurrentMoveState(MoveState currentMoveState, int startIndexRow, int startIndexCol, int endIndexRow, int endIndexCol, 
			char[][] updatedPositionArray, String moveType, String startPos, String endPos, char[][] changedPositionArray, int maxEval) {
		currentMoveState.setStartIndexRow(startIndexRow);
		currentMoveState.setStartIndexCol(startIndexCol);
		currentMoveState.setEndIndexRow(endIndexRow);
		currentMoveState.setEndIndexCol(endIndexCol);
		currentMoveState.setMaxEvalValue(maxEval);
		currentMoveState.setUpdatedPositionArray(updatedPositionArray);
		currentMoveState.setMoveType(moveType);
		currentMoveState.setStartPosition(startPos);
		currentMoveState.setEndPosition(endPos);
		currentMoveState.setChangedPositionArray(changedPositionArray);
		return currentMoveState;
	}
	
	/**
	 * @param moveState
	 * @param oppoPlayerSymbol 
	 * @param inBean
	 * @return positionArray
	 */
	public char[][] changeBoard(MoveState moveState, char[][] positionArray, char myPlayerSymbol, char oppoPlayerSymbol) {
		//char[][] positionArray = inBean.getGraphArr();
		if(moveState.getMoveType().equals(Constants.VERTICALLY_UPWARD)) {
			for (int i = moveState.getStartIndexRow(); i >= moveState.getEndIndexRow(); i--) {
				positionArray[i][moveState.getStartIndexCol()] = myPlayerSymbol;
			}
		}
		if(moveState.getMoveType().equals(Constants.VERTICALLY_DOWNWARD)) {
			for (int i = moveState.getStartIndexRow(); i <= moveState.getEndIndexRow(); i++) {
				positionArray[i][moveState.getStartIndexCol()] = myPlayerSymbol;
			}
		}
		if(moveState.getMoveType().equals(Constants.DIAGONALLY_LEFT_UPWARD)) {
			for (int i = moveState.getStartIndexRow(), j = moveState.getStartIndexCol(); i >= moveState.getEndIndexRow(); i--) {
					positionArray[i][j] = myPlayerSymbol;
					j--;
			}
		}
		if(moveState.getMoveType().equals(Constants.DIAGONALLY_RIGHT_UPWARD)) {
			for (int i = moveState.getStartIndexRow(), j = moveState.getStartIndexCol(); i >= moveState.getEndIndexRow(); i--) {
				positionArray[i][j] = myPlayerSymbol;
				j++;
			}
		}
		if(moveState.getMoveType().equals(Constants.DIAGONALLY_LEFT_DOWNWARD)) {
			for (int i = moveState.getStartIndexRow(), j = moveState.getStartIndexCol(); i <= moveState.getEndIndexRow(); i++) {
				positionArray[i][j] = myPlayerSymbol;
				j--;
			}
		}
		if(moveState.getMoveType().equals(Constants.DIAGONALLY_RIGHT_DOWNWARD)) {
			for (int i = moveState.getStartIndexRow(), j = moveState.getStartIndexCol(); i <= moveState.getEndIndexRow(); i++) {
				positionArray[i][j] = myPlayerSymbol;
				j++;
			}
		}
		if(moveState.getMoveType().equals(Constants.HORIZONTALLY_LEFT)) {
			for (int j = moveState.getStartIndexCol(); j >= moveState.getEndIndexCol(); j--) {
				positionArray[moveState.getStartIndexRow()][j] = myPlayerSymbol;
			}
		}
		if(moveState.getMoveType().equals(Constants.HORIZONTALLY_RIGHT)) {
			for (int j = moveState.getStartIndexCol(); j <= moveState.getEndIndexCol(); j++) {
				positionArray[moveState.getStartIndexRow()][j] = myPlayerSymbol;
			}
		}
		moveState.setChangedPositionArray(positionArray);
		char[][] positionArrayFinal = updateBoard(moveState, myPlayerSymbol, oppoPlayerSymbol);
			
		return positionArrayFinal;
	}
	
	public char[][] updateBoard(MoveState moveState, char myPlayerSymbol, char oppoPlayerSymbol) {
		
		boolean checkVerticallyUpward = false;//1
		boolean checkDiagonallyLeftUpward = false;//2
		boolean checkDiagonallyRightUpward = false;//3
		boolean checkVerticallyDownward = false;//4
		boolean checkDiagonallyLeftDownward= false;//5
		boolean checkDiagonallyRightDownward = false;//6
		boolean checkHorizontallyLeft = false;//7
		boolean checkHorizontallyRight = false;//8
		
		int i = moveState.getEndIndexRow();
		int j = moveState.getEndIndexCol();
		char[][] positionArray = moveState.getChangedPositionArray();
		
		char[][] positionArrayTemp = new char[8][8];
		for (int k = 0; k < positionArray.length; k++) {
			for (int k2 = 0; k2 < positionArray.length; k2++) {
				positionArrayTemp[k][k2] = positionArray[k][k2];
			}
		}
		
		if (i != 0 && j != 0) {
			if (positionArrayTemp[i - 1][j - 1] == oppoPlayerSymbol) {// 1
				checkDiagonallyLeftUpward = true;
			}
		}
		// 1
		if (checkDiagonallyLeftUpward) {
			for (int k = i - 2, k2 = j - 2; k >= 0; k--) {
				if (k2 >= 0) {
					if (positionArrayTemp[k][k2] == myPlayerSymbol) {
						for (int l = i - 1, m=j-1; l >= k+1; l--) {
							if(m > k2) {
								positionArrayTemp[l][m] = myPlayerSymbol;
							}
							m--;
						}
						break;
					}
					else if(positionArrayTemp[k][k2] == this.blankSymbol) {
						break;
					}
					k2--;
				}
			}
		}
		
		if(i != 0) {
			if(positionArrayTemp[i-1][j] == oppoPlayerSymbol) {//2
				checkVerticallyUpward= true;
			}
		}
		//2
		
		if (checkVerticallyUpward) {
			for (int k = i-2; k >= 0; k--) {
				if (positionArrayTemp[k][j] == myPlayerSymbol) {
					for (int k2 = i-1; k2 >= k+1; k2--) {
						positionArrayTemp[k2][j] = myPlayerSymbol;
					}
				break;
				}
				else if(positionArrayTemp[k][j] == this.blankSymbol) {
					break;
				}
			}
		}
				
		
		if (i != 0 && j != positionArrayTemp.length - 1) {
			if (positionArrayTemp[i - 1][j + 1] == oppoPlayerSymbol) {// 3
				checkDiagonallyRightUpward = true;
			}
		}
		// 3
		if (checkDiagonallyRightUpward) {
			for (int k = i-2, k2 = j+2; k>=0; k--) {
				if (k2 < positionArrayTemp.length) {
					if (positionArrayTemp[k][k2] == myPlayerSymbol) {
						for (int l = i-1, m=j+1; l >= k+1; l--) {
							if (m < k2) {
								positionArrayTemp[l][m] = myPlayerSymbol;
							}
							m++;
						}
						break;
					}
					else if (positionArrayTemp[k][k2] == this.blankSymbol) {
						break;
					}
					k2++;
				}
			}
		}
		
		if (j != 0) {
			if (positionArrayTemp[i][j - 1] == oppoPlayerSymbol) {// 4
				checkHorizontallyLeft = true;
			}
		}
		//4
		if (checkHorizontallyLeft) {
			for (int k = j-2; k >= 0; k--) {
				if (positionArrayTemp[i][k] == myPlayerSymbol) {
					for (int k2 = j-1; k2 >= k+1; k2--) {
						positionArrayTemp[i][k2] = myPlayerSymbol;
					}
				break;
				}
				else if (positionArrayTemp[i][k] == this.blankSymbol) {
					break;
				}
			}
		}		
		
		
		if (j != positionArrayTemp.length - 1) {
			if (positionArrayTemp[i][j + 1] == oppoPlayerSymbol) {// 5
				checkHorizontallyRight = true;
			}
		}

		// 5
		if (checkHorizontallyRight) {
			for (int k = j+2; k < positionArrayTemp.length; k++) {
				if (positionArrayTemp[i][k] == myPlayerSymbol) {
					for (int k2= j+1; k2 <= k-1; k2++) {
						positionArrayTemp[i][k2] = myPlayerSymbol;
					}
				break;
				}
				else if (positionArrayTemp[i][k] == this.blankSymbol) {
					break;
				}
			}
		}

		if (i != positionArrayTemp.length - 1 && j != 0) {
			if (positionArrayTemp[i + 1][j - 1] == oppoPlayerSymbol) {// 6
				checkDiagonallyLeftDownward = true;
			}
		}
		// 6
		if (checkDiagonallyLeftDownward) {
			for (int k= i+2, k2 = j-2; k<positionArrayTemp.length; k++) {
				if (k2 >= 0) {
					if (positionArrayTemp[k][k2] == myPlayerSymbol) {
						for (int l=i+1, m=j-1; l <= k-1; l++) {
							if (m > k2) {
								positionArrayTemp[l][m] = myPlayerSymbol;
							}
							m--;
						}
					break;
					}
					else if (positionArrayTemp[k][k2] == this.blankSymbol) {
						break;
					}
					k2--;
				}
			}
		}
		
		if (i != positionArrayTemp.length - 1) {
			if (positionArrayTemp[i + 1][j] == oppoPlayerSymbol) {// 7
				checkVerticallyDownward = true;
			}
		}

		// 7
		if (checkVerticallyDownward) {
			for (int k =i+2; k < positionArrayTemp.length; k++) {
				if (positionArrayTemp[k][j] == myPlayerSymbol) {
					for (int k2 = i+1; k2 <= k-1; k2++) {
						positionArrayTemp[k2][j] = myPlayerSymbol;
					}
					break;
				}
				else if (positionArrayTemp[k][j] == this.blankSymbol) {
					break;
				}
			}
		}
		
		if (i != positionArrayTemp.length - 1 && j != positionArrayTemp.length - 1) {
			if (positionArrayTemp[i + 1][j + 1] == oppoPlayerSymbol) {// 8
				checkDiagonallyRightDownward = true;
			}
		}

		// 8
		if (checkDiagonallyRightDownward) {
			for (int k = i + 2, k2 = j + 2; k < positionArrayTemp.length; k++) {
				if (k2 < positionArrayTemp.length) {
					if (positionArrayTemp[k][k2] == myPlayerSymbol) {
						for (int l = i+1, m=j+1; l <= k-1; l++) {
							if (m < k2) {
								positionArrayTemp[l][m] = myPlayerSymbol;
							}
							m++;
						}
						break;
					}
					else if (positionArrayTemp[k][k2] == this.blankSymbol) {
						break;
					}
					k2++;
				}
			}
		}
		return positionArrayTemp;
	}
	
}
