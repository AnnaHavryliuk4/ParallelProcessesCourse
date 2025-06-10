#include <iostream>
#include <omp.h>
#include <vector>
#include <cstdlib>
#include <ctime>
#include <climits>

const int ROWS = 1000;
const int COLS = 1000;

void fillArray(std::vector<std::vector<int>>& matrix) {
    #pragma omp parallel for
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            matrix[i][j] = rand() % 100;
        }
    }
}

long long calculateTotalSum(const std::vector<std::vector<int>>& matrix) {
    long long totalSum = 0;
    #pragma omp parallel for reduction(+:totalSum)
    for (int i = 0; i < ROWS; i++) {
        for (int j = 0; j < COLS; j++) {
            totalSum += matrix[i][j];
        }
    }
    return totalSum;
}

void findMinRow(const std::vector<std::vector<int>>& matrix, int& minRowIndex, long long& minRowSum) {
    minRowSum = LLONG_MAX;
    minRowIndex = -1;

    #pragma omp parallel
    {
        int localMinRow = -1;
        long long localMinSum = LLONG_MAX;

        #pragma omp for
        for (int i = 0; i < ROWS; i++) {
            long long sum = 0;
            for (int j = 0; j < COLS; j++) {
                sum += matrix[i][j];
            }
            if (sum < localMinSum) {
                localMinSum = sum;
                localMinRow = i;
            }
        }

        #pragma omp critical
        {
            if (localMinSum < minRowSum) {
                minRowSum = localMinSum;
                minRowIndex = localMinRow;
            }
        }
    }
}

int main() {
    std::vector<std::vector<int>> matrix(ROWS, std::vector<int>(COLS));

    srand((unsigned)time(0));
    fillArray(matrix);

    int minRowIndex;
    long long totalSum = 0, minRowSum = 0;

    double start = omp_get_wtime();

    #pragma omp parallel sections
    {
        #pragma omp section
        {
            totalSum = calculateTotalSum(matrix);
        }

        #pragma omp section
        {
            findMinRow(matrix, minRowIndex, minRowSum);
        }
    }

    double end = omp_get_wtime();

    std::cout << "Total sum: " << totalSum << "\n";
    std::cout << "Row with minimum sum: " << minRowIndex << ", sum: " << minRowSum << "\n";
    std::cout << "Time taken: " << (end - start) << " seconds\n";

    return 0;
}
