import numpy
n = 8
board = numpy.zeros((n, n), dtype=int)
diff_lis = numpy.array([n]*n)   #for (0, 0), (1, 1) ..., index difference is 0, for (1, 0), (2, 1) ... it is 1, etc. Quick diagonal check possible.
sum_lis = numpy.array([2*n]*n)  #sum of indices, used for other diagonal quick check.
row_lis = numpy.array([-1]*n)   #row index of each queen, for quick row check.


def canBePlaced(row, col): #row, col correspond to board[row][col]
    assert (row <= n) and (row >= 0) and (col <= n ) and (col >= 0)
    s = (row + col) #sum
    d = (row - col) #difference
    for i in range(col): #check columns 0, ..., (current column - 1) to avoid self-comparison
        if (diff_lis[i] == d) or (sum_lis[i] == s) or (row_lis[i] == row):
            return False #COLLISION HAPPENED!
    return True

def placeAt(row, col):
    diff_lis[col] = row - col
    sum_lis[col] = row + col
    row_lis[col] = row
    board[row][col] = 1
def removeFrom(row, col):
    diff_lis[col] = n
    sum_lis[col] = 2*n
    row_lis[col] = -1
    board[row][col] = 0
def printBoard():
    for row in board:
        #print(list(map(str, row)).join(" "))
        print(" ".join(list(map(str, row))))
    print()
"""
for a given column, for each row, check if placing a queen there would result in a legal config (canBePlaced). If yes, then we can try
placing a queen there and try next column (tryColumn). It should return True if a solution is reached.
"""

def tryColumn(col): #given column index 0 ... n-1, try to place queen there. Return true if placement successful (and modify board), else False
    if col == n:
        return True
    for row in range(n):
        if canBePlaced(row, col):
            placeAt(row, col)
            if tryColumn(col+1):
                return True #solution found, so exit recursive column-check.
            else: #the queen placed in this row does not lead to a solution.
                removeFrom(row, col)

tryColumn(0)
#print(board)
printBoard()       

