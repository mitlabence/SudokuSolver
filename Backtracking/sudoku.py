import numpy as np

#TODO: implement mask to protect entries from overwrite. Need to modify function isValid()!

class Puzzle:
    """The Puzzle class describes a Sudoku board.

    The entries are stored in a numpy array. 0 means no entry, 1-9 mark the corresponding entry.
    The indexing convention is as follows: the top-left element on the real sudoku board has index (0, 0). The one below it (1, 0), i.e. the row index
    corresponds to the rows on the board.
    There are 9 3x3 segments: these are indexed similarly to the elements, i.e. (0, 0) is the (vertical, horizontal) pair of indices corresponding to
    the top left 3x3 segment.
    """
    def __init__(self, initial_board = np.zeros((9, 9))): #takes 9x9 matrix filled with integers 0-9: 1-9 for actual values, 0 for empty cell.
        self.board = np.copy(initial_board)
        self.segment_map = {0: (0, 3), 1: (3, 6), 2: (6, 9)} #mapping from segment description to index range
    def getSegment(self, horizontal_segment: int=0, vertical_segment: int=0):#horizontal: 0 is left, 1 is middle, 2 is right; vertical: 0 is top, 1 is middle, 2 is bottom
        try:
            begin_x, end_x = self.segment_map[horizontal_segment]
            begin_y, end_y = self.segment_map[vertical_segment]
        except KeyError:
            print(f"Failed to fetch segment w/ vertical index {vertical_segment}, horizontal index {horizontal_segment}.")
        return self.board[begin_x:end_x, begin_y:end_y] #return 2D array
    def getSegmentFlattened(self, horizontal: int=0, vertical: int=0):
        return self.getSegment(horizontal, vertical).flatten()
    def getRow(self, row: int=0):
        return np.copy(self.board[row])
    def getColumn(self, col: int=0):
        return np.copy(self.board[:, col])
    def whichSegment(row, col):
        horizontal_segment = (col - col%3)//3   #falls between 0 and 2 (inclusive). 0 is left, 1 is middle, 2 is right.
        vertical_segment = (row - row%3)//3     #0 is top, 1 is middle, 2 is bottom
        return (vertical_segment, horizontal_segment)
    def isProtected(row: int, col: int) -> bool: #TODO: implement feature using a constant self.mask
        """Checks if cell is overwrite-protected. Returns True if yes, returns False if cell can be overwritten.
        """
        return False
    def isValid(self, entry: int, row: int, col: int):
        """returns True if writing entry at location (row, col) does not result in collision in row, column or segment.
        """
        if entry != 0: #writing 0 is erasing.
            if entry in self.getRow(row):
                return False #element already in same row
            elif entry in self.getColumn(col):
                return False #element already in same column
            elif entry in self.getSegment(self.whichSegment(row, col)):
                return False #element already in same segment
        return True
    def writeCell(self, entry: int, row: int, col: int):
        """Overwrite element in cell with index (row, col) if not overwrite-protected.
        """
        if not isProtected(row, col):
            self.board[row][col] = entry
    def cellAt(self, row: int, col: int):
        """
        Use this function to return board element
        """
        try:
            return self.board[row][col]
        except IndexError:
            print(f"Out of bounds while accessing cellAt({row}, {col})!")
    #Test functions
    def _TESTsegment(self) -> None:
        print("This should be the whole board:")
        for row in self.board:
            print(" ".join(map(str, row)))
        print("\nThere should be the segments, left to right, top to bottom:")
        for i in range(3):
            for j in range(3):
                print(f"\n{i}, {j}:")
                segment = self.getSegment(i, j)
                for row in segment:
                    print(" ".join(map(str, row)))

def solvePuzzle(board: Puzzle):
    row = 0
    col = 0
    while(board.cellAt(row, col) != 0):
        row = row + 1 #find first empty cell 
        
example_board = [[0, 3, 0, 0, 0, 0, 0, 0, 0],
                 [0, 2, 0, 9, 0, 6, 3, 0, 0],
                 [0, 6, 0, 4, 0, 2, 0, 9, 0],
                 [1, 0, 0, 0, 9, 0, 4, 0, 0],
                 [0, 0, 8, 1, 0, 3, 5, 0, 0],
                 [0, 5, 0, 3, 0, 1, 0, 6, 0],
                 [0, 0, 4, 6, 0, 7, 0, 3, 0],
                 [0, 0, 0, 0, 0, 0, 0, 8, 0]
]
p = Puzzle(example_board)