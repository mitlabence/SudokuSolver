import numpy as np

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
    def getSegment(self, vertical_segment: int=0, horizontal_segment: int=0):#horizontal: 0 is left, 1 is middle, 2 is right; vertical: 0 is top, 1 is middle, 2 is bottom
        try:
            begin_x, end_x = self.segment_map[horizontal_segment]
            begin_y, end_y = self.segment_map[vertical_segment]
        except KeyError:
            print(f"Failed to fetch segment w/ vertical index {vertical_segment}, horizontal index {horizontal_segment}.")
        for y in range(begin_y, end_y):
            for x in range(begin_x, end_x):
                yield self.board[y][x] #return elements of 3x3 segment, row by row
    def nextEmptyInRow(self, row: int, col: int):
        """Find next empty position in row starting with (inclusive) (row, col) location. Return index if found one, return 9 if none found.
        """
        x = col
        while (x < 9) and (self.board[row][x] != 0):
            x = x + 1
        return x

    def getRow(self, row: int=0): #generator for one row, i.e. self.board[row, :]
        for i in range(9):
            yield self.board[row][i]
    def getColumn(self, col: int=0):
        for i in range(9):
            yield self.board[i][col]
        #return np.copy(self.board[:, col])
    def whichSegment(self, row: int, col: int):
        horizontal_segment = (col - col%3)//3   #falls between 0 and 2 (inclusive). 0 is left, 1 is middle, 2 is right.
        vertical_segment = (row - row%3)//3     #0 is top, 1 is middle, 2 is bottom
        return (vertical_segment, horizontal_segment)
    def isProtected(self, row: int, col: int) -> bool: #TODO: implement feature using a constant self.mask
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
            elif entry in self.getSegment(*self.whichSegment(row, col)):
                return False #element already in same segment
        return True
    def writeCell(self, entry: int, row: int, col: int):
        """Overwrite element in cell with index (row, col) if not overwrite-protected.
        """
        if not self.isProtected(row, col):
            self.board[row][col] = entry
    def cellAt(self, row: int, col: int):
        """
        Use this function to return board element
        """
        try:
            return self.board[row][col]
        except IndexError:
            print(f"Out of bounds while accessing cellAt({row}, {col})!")
    def drawBoard(self):
        """Draws board in human-friendly form.
        """
        for row in self.board:
            print("\n" + " ".join(row))
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
#TODO: Think about logic here again
def fillCell(board: Puzzle, row: int, col: int):
    """fills single cell correctly; keeps iterating until meets irresolvable situation in row, or end of row. Should be only called on empty cell!
    """
    if col == 9:
        return True #row has been filled successfully
    if board.board[row][col] != 0: #guard against non-empty cell being written to. TODO: remove once protection is built into Puzzle class
        print("Error! Non-empty cell written to!")
    for guess in range(1, 10):
        if board.isValid(guess, row, col):
            board.writeCell(guess, row, col)
            if fillCell(board, row, board.nextEmptyInRow(row, col+1)) == True:
                return True
    board.writeCell(0, row, col) #set back to empty
    return False
    
def fillRow(board: Puzzle, row: int):
    """Attempts to fill row, and continues iteratively until reaches irresolvable situation, or the last row is successfully filled.
    """
    if row == 9: #reached bottom of Sudoku, with all rows filled successfully
        return True
    if fillCell(board, row, board.nextEmptyInRow(row, 0)): #succeeded in filling all cells in a row, proceed to next one
        if fillRow(board, row + 1):
            return True
    return False       
        
example_board = [[0, 0, 0, 2, 6, 0, 7, 0, 1],
                 [6, 8, 0, 0, 7, 0, 0, 9, 0],
                 [1, 9, 0, 0, 0, 4, 5, 0, 0],
                 [8, 2, 0, 1, 0, 0, 0, 4, 0],
                 [0, 0, 4, 6, 0, 2, 9, 0, 0],
                 [0, 5, 0, 0, 0, 3, 0, 2, 8],
                 [0, 0, 9, 3, 0, 0, 0, 7, 4],
                 [0, 4, 0, 0, 5, 0, 0, 3, 6],
                 [7, 0, 3, 0, 1, 8, 0, 0, 0]
]
example_solution = [[4, 3, 5, 2, 6, 9, 7, 8, 1],
                    [6, 8, 2, 5, 7, 1, 4, 9, 3],
                    [1, 9, 7, 8, 3, 4, 5, 6, 2],
                    [8, 2, 6, 1, 9, 5, 3, 4, 7],
                    [3, 7, 4, 6, 8, 2, 9, 1, 5],
                    [9, 5, 1, 7, 4, 3, 6, 2, 8],
                    [5, 1, 9, 3, 2, 6, 8, 7, 4],
                    [2, 4, 8, 9, 5, 7, 1, 3, 6],
                    [7, 6, 3, 4, 1, 8, 2, 5, 9]
]
"""
Call fillRow(p, 0) to start solving the Sudoku-puzzle p.
"""
from time import time
t0 = time()
p = Puzzle(example_board)
fillRow(p,0)
t = time()
print(p.board)