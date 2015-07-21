/* DisplayPersonsByNameStatus.java
 */

/* DisplayPersonsByNameStatus
 */
public class DisplayPersonsByNameStatus extends ConsoleStatus {

	// フィールド
	private String name;
	private PersonList plist;
	private PersonList selectedList;
	private DisplayPersonStatus next;
	private int next_start_id = 0; 	// 次のページの先頭ID
	private int start_id;						// 現ページの先頭ID
	private int listsize;						// selectedListのレコード数

	/**
	 * コンストラクタ DisplayPersonsByNameStatus
	 * @param String firstMess
	 * @param String promptMess
	 * @param boolean IsEndStatus
	 * @param PersonList plist
	 * @param DisplayPersonStatus next
	 */
	DisplayPersonsByNameStatus( String firstMess, String promptMess,
	                     boolean IsEndStatus,
	                     PersonList plist, DisplayPersonStatus next ) {
		super( firstMess, promptMess, IsEndStatus );
		this.name = "";
		this.plist = plist;
		this.selectedList = null;
		this.next = next;
	}

	// 最初に出力するメッセージを表示する
	/** displayFirstMess
	 * @throws Exception
	 */
	public void displayFirstMess() throws Exception {
		displayList(" ");
		super.displayFirstMess();
	}

	// 検索する氏名を登録する
	/** setName
	 * @param String name
	 */
	public void setName( String name ) {
		this.name = name;
	}

	// 入力された氏名の文字列を氏名に含む従業員のレコードだけを
	// 取り出す処理
	/**
	 * displayList
	 */
	public void displayList(String code) {
		// 入力された氏名に一致または氏名を含む従業員のレコードだけを
		// selectedListに取り出す
		if(next_start_id == 0){
			selectedList = plist.searchByName( name );
			listsize = selectedList.size();
		}
		// selectedListの件数＝0ならば当該職種をもつ
		// 従業員はいないと表示
		if( listsize <= 0 )
			System.out.println( "従業員が存在しません。" );
		else{
			if(code.equals(" ") && next_start_id == 0){
				System.out.println("最初のページを表示");
				int rows = listsize >= 3 ? 3 : listsize;
				for(int i=0; i<rows; i++){
					System.out.println( selectedList.getRecord(i).toString() );
				}
				start_id = next_start_id;
				next_start_id = rows;
			}else if(code.equals("N")){
				if(listsize > next_start_id){
					System.out.println("次のページを表示");
					int rows = listsize- next_start_id >= 3 ? 
																		3 : listsize - next_start_id;
					for(int i=next_start_id; i<next_start_id+rows; i++){
						System.out.println( selectedList.getRecord(i).toString() );
					}
					start_id = next_start_id;
					next_start_id += rows;
				}else{
					System.out.println("最後まで表示して頭に戻りました");
					int rows = listsize >= 3 ? 3 : listsize;
					for(int i=0; i<rows; i++){
						System.out.println( selectedList.getRecord(i).toString() );
					}
					start_id = 0;
					next_start_id = rows;
				}
			}else if(code.equals("P")){
				System.out.println("next_start_id: " + next_start_id);
				System.out.println("start_id: " + start_id);
				if(start_id >= 3){ // 前に３件以上なければ末尾の３件
					System.out.println("前のページを表示");
					if(next_start_id >= 6){ // 6件を超える場合は、現ページの先頭から3を引いたIDから表示
						next_start_id = start_id - 3;
					}else{ // 6件までは先頭ページ
						next_start_id = 0;
					}
					for(int i=next_start_id; i<next_start_id+3; i++){
						System.out.println( selectedList.getRecord(i).toString() );
					}
					start_id = next_start_id;
					next_start_id += 3;
				}else{
					System.out.println("末尾の３件を表示");
					next_start_id = 
						listsize >= 3 ? listsize-3 : 0;
					for(int i=next_start_id; i<listsize; i++){
						System.out.println( selectedList.getRecord(i).toString() );
					}
					start_id = next_start_id;
					next_start_id = listsize;
				}
			}
		}
	}

	// 次の状態に遷移することを促すためのメッセージの表示
	/** getNextStatus
	 * @param String s
	 * @return ConsoleStatus
	 */
	public ConsoleStatus getNextStatus( String s ) {
		if(s.equals("N") || s.equals("P")){
			// N -> 次の３件、P -> 前の３件
			displayList(s);
			return this;
		}else{
			// 一覧表示に戻った時は先頭から表示
			start_id = 0;
			next_start_id = 0;
			// 数値が入力された場合，その数値と同じIDをもつ
			// レコードがselectedListにあるかどうか判定し，
			// あればそれを次の状態DisplayPersonStatusに渡す
			try {
				int i = Integer.parseInt( s );
				Person p = selectedList.get( i );
				if( p == null )
					return this;
				else {
					next.setPersonRecord( p );
					return next;
				}
			} catch( NumberFormatException e ) {
				return super.getNextStatus( s );
			}
		}
	}
}
