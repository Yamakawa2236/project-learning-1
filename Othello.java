public class Othello {
	private String [][] grids = new String [8][8]; //局面情報
	private String turn; //手番

	// コンストラクタ
	public Othello(){
		turn = "black"; //黒が先手
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				grids[i][j] = "board";
			}
		}
		grids[3][3] = "black";
		grids[3][4] = "white";
		grids[4][3] = "white";
		grids[4][4] = "black";
	}

	// メソッド
	public String checkWinner(){	// 勝敗を判断
		int countBlack = 0;
		int countWhite = 0;
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(grids[i][j].equals("black")) {
					countBlack++;
				}else if(grids[i][j].equals("white")) {
					countWhite++;
				}
			}
		}
		if(countBlack > countWhite) {
			return "black";
		}else if(countBlack < countWhite) {
			return "white";
		}else {
			return "even";
		}
	}

	public String getTurn(){ // 手番情報を取得
		return turn;
	}

	public String [][] getGrids(){ // 局面情報を取得
		return grids;
	}

	public void changeTurn(){ //　手番を変更
		if(turn.equals("black")) {
			turn = "white";
		}else {
			turn = "black";
		}
	}

	public String gameState(String color) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(grids[i][j].equals("board")) {
					if(putStone(j, i,color,false)) {
						return "go";
					}
				}
			}
		}
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(grids[i][j].equals("board")) {
					if(putStone(j,i,revColor,false)) {
						return "pass";
					}
				}
			}
		}
		return "end";
	}

	public boolean turnLeftUp(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(y>1 && x>1) {
			String next = grids[y-1][x-1];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(x-i<0 || y-i<0 || grids[y-i][x-i].equals("board")) {
						break;
					}else if(grids[y-i][x-i].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y-j][x-j] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnUp(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color == "black") {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(y>1) {
			String next = grids[y-1][x];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(y-i<0 || grids[y-i][x].equals("board")) {
						break;
					}else if(grids[y-i][x].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y-j][x] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnRightUp(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(y>1 && x<6) {
			String next = grids[y-1][x+1];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(x+i>7 || y-i<0 || grids[y-i][x+i].equals("board")) {
						break;
					}else if(grids[y-i][x+i].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y-j][x+j] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnRight(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(x<6) {
			String next = grids[y][x+1];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(x+i>7 || grids[y][x+i].equals("board")) {
						break;
					}else if(grids[y][x+i].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y][x+j] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnRightDown(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(y<6 && x<6) {
			String next = grids[y+1][x+1];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(x+i>7 || y+i>7 || grids[y+i][x+i].equals("board")) {
						break;
					}else if(grids[y+i][x+i].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y+j][x+j] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnDown(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(y<6) {
			String next = grids[y+1][x];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(y+i>7 || grids[y+i][x].equals("board")) {
						break;
					}else if(grids[y+i][x].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y+j][x] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnLeftDown(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(y<6 && x>1) {
			String next = grids[y+1][x-1];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(x-i<0 || y+i>7 || grids[y+i][x-i].equals("board")) {
						break;
					}else if(grids[y+i][x-i].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y+j][x-j] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean turnLeft(int x,int y,String color,boolean effect_on) {
		String revColor;
		if(color.equals("black")) {
			revColor = "white";
		}
		else {
			revColor = "black";
		}
		boolean flag = false;
		if(x>1) {
			String next = grids[y][x-1];
			if(next.equals(revColor)) {
				for(int i=2;true;i++) {
					if(x-i<0 || grids[y][x-i].equals("board")) {
						break;
					}else if(grids[y][x-i].equals(color)) {
						if(effect_on) {
							for(int j=0;j<i;j++) {
								grids[y][x-j] = color;
							}
						}
						flag = true;
						break;
					}
				}
			}
		}
		if(flag) {
			return true;
		}else {
			return false;
		}
	}

	public boolean putStone(int x,int y, String color,boolean effect_on){ // (操作を)局面に反映
		boolean flag = false;
		if(!grids[y][x].equals("board")) {
			return false;
		}else {
			if(turnDown(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnLeft(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnLeftDown(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnLeftUp(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnRight(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnRightDown(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnRightUp(x, y, color,effect_on)) {
				flag = true;
			}
			if(turnUp(x, y, color,effect_on)) {
				flag = true;
			}
			if(flag) {
				return true;
			}else {
				return false;
			}
		}
	}
}