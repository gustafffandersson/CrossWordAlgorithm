import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class CrossWordProducer {
    private int rowMin = 1000000;
    private int colMin = 1000000;
    private int rowMax = 0;
    private int colMax = 0;
    private char[][] m;

    public CrossWordProducer() {
        super();
    }

    public char[][] generateCrossWordMatrix(ArrayList<String> list) {
        // Sort list. Longest words first.
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.length() - s1.length();
            }
        });

        

        // Calculate worst case (largest) dimensions for out matrix. (Will be trimmed in the end)
        int n = 0;
        for (int i = 0; i < list.size(); i++) {
            n += list.get(i).length();
        }
        n *= 2;

        // Construct empty matrix
        m = new char[n][n];

        // Generate first word and place it in the matrix.
        ArrayList<Letter> firstWord = genFirstWord(list.get(0), n);
        placeWord(firstWord); 
        list.remove(0);

        ArrayList<Letter> placableWord;
        while (true) {
            boolean wordPlaced = false;
            for (String s : list) {
                placableWord = findPlaceForWord(s);
                if (placableWord != null) {
                    placeWord(placableWord);
                    wordPlaced = true;
                    list.remove(s); 
                    break;
                }
            }
            // print
            //for (int i=0; i < n; i++) {
            //    for (int j=0; j < n; j++) {
            //        System.out.print(m[i][j] + " ");
            //    }
            //    System.out.println();
            //}

            if (!wordPlaced) {
                break;
            }
        }
        for (int i=rowMin; i <= rowMax; i++) {
            for (int j=colMin; j <= colMax; j++) {
                if (m[i][j] == '\u0000') {
                    System.out.print("  ");
                } else {
                    System.out.print(m[i][j] + " ");
                }
            }
            System.out.println();
        }
        return null;
    }

    private ArrayList<Letter> findPlaceForWord(String s){

        HashSet<Character> letters = new HashSet<Character>();
        for (char c : s.toCharArray()) { // todo: “foreach“
            letters.add(c);
        }
            
        // iterate over used part of matrix: 
        for (int r = rowMin; r <= rowMax /* < or <= ??>*/; r++) {
            for (int c = colMin; c <= colMax /* < or <= ??>*/; c++) {
                // If element not set, skip.
                if (m[r][c] == '\u0000') {
                    continue;
                } 
                // not an interesting char
                if (!letters.contains(m[r][c])) {
                    continue;
                }
                    
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) != m[r][c]) {
                        continue;
                    }
                        
                    boolean placementOk = true;
                    // Look vertically
                    int charIndex = 0;
                    for (int j = r-i; j < r-i+s.length(); j++) {
                        //if (j == r) {
                        //    charIndex++;
                        //    continue;
                        //}
                        if(!checkCell(j, c, s, charIndex, 1)) {
                            placementOk = false;
                            break;
                        }
                        charIndex++;
                    }

                    // if ok: Return the placement
                    if (placementOk) {
                        ArrayList<Letter> placement = new ArrayList<Letter>();
                        int k = r-i;
                        for (char ch : s.toCharArray()) {
                            placement.add(new Letter(k, c, ch));
                            k++;
                        }
                        return placement;
                    }
                        
                    // check horizontally
                    placementOk = true;
                    charIndex = 0;
                    for (int j = c-i; j < c-i+s.length(); j++) {
                        //if (j == c) {
                        //    charIndex++;
                        //    continue;
                        //}
                        if(!checkCell(r, j, s, charIndex, 0)) {
                            placementOk = false;
                            break;
                        }
                        charIndex++;
                    }

                    // if ok: Return the placement
                    if (placementOk) {
                        ArrayList<Letter> placement = new ArrayList<Letter>();
                        int k = c-i;
                        for (char ch : s.toCharArray()) {
                            placement.add(new Letter(r, k, ch));
                            k++;
                        }
                        return placement;
                    }    
                }   
            }
        }
        return null;
    }
            
    /**
     * Check if a cell is valid for placing a char.
     * @param x
     * @param y
     * @param s
     * @param index
     * @param direction 0=horisontal, 1=vertical
     * @return
     */
    private boolean checkCell(int row, int col, String s, int index, int direction) {
        // center
        if (m[row][col] != s.charAt(index) && m[row][col] != '\u0000') {
             return false;
        }
                
        // Left 
        if (m[row][col-1] != '\u0000'){
            if (direction == 1) {
                if (m[row][col] != s.charAt(index)) {
                    return false;
                }
            } else {
                if (index == 0 || s.charAt(index-1) != m[row][col-1]) {
                    return false;
                }
            }
        }
                
        // up
        if (m[row-1][col] != '\u0000') {
            if (direction == 1) {
                if (index == 0 || s.charAt(index-1) != m[row-1][col]) {
                    return false;
                }
            } else if (m[row][col] != s.charAt(index)){
                return false;
            }
        }
                
        // right
        if (m[row][col+1] != '\u0000') {
            if (direction == 0) {
                if (index == s.length()-1 || s.charAt(index+1) != m[row][col+1]) {
                    return false;
                }
            } else  if (m[row][col] != s.charAt(index)){
                return false;
            }
        }
                
        // down
        if (m[row+1][col] != '\u0000') {
            if (direction == 1) {
                if (index == s.length()-1 || s.charAt(index+1) != m[row+1][col]) {
                    return false;
                } 
            } else if (m[row][col] != s.charAt(index)) {
                    return false;
            }
        }
         return true;
    }

    /**
     * Place a list of Letter on the matrix and update min max values.
     * @param word
     */
    private void placeWord(ArrayList<Letter> word) {
        for (Letter l : word) {
            m[l.getRow()][l.getCol()] = l.getLetter();
        }
        rowMin = Math.min(rowMin, word.get(0).getRow());
        rowMax = Math.max(rowMax, word.get(word.size()-1).getRow());
        colMin = Math.min(colMin, word.get(0).getCol());
        colMax = Math.max(colMax, word.get(word.size()-1).getCol());
    }

    /**
     * Generate the first word (list of Letters). First word is horizontal.
     * @param firstWordString
     * @param matrixDim
     * @return
     */
    private ArrayList<Letter> genFirstWord(String firstWordString, int matrixDim) {
        ArrayList<Letter> word = new ArrayList<Letter>();
        int col = matrixDim/2 - firstWordString.length()/2;
        int row = matrixDim/2;
        for (int i=0; i < firstWordString.length(); i++) {
            word.add(new Letter(row, col, firstWordString.charAt(i)));
            col += 1;
        }
        return word;
    }

    public static void main(String[] args) {
        CrossWordProducer cwp = new CrossWordProducer();
        ArrayList<String> list = new ArrayList<String>();
        list.add("pirat");
        list.add("bengal");
        list.add("tolv");
        list.add("tjugofyra");
        list.add("tusen");
        list.add("alvarligt");
        list.add("sju");
        list.add("nittiotre");
        list.add("abslout");
        list.add("abrakadabra");
        cwp.generateCrossWordMatrix(list);
    }

}