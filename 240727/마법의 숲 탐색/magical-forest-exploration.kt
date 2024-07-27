import java.util.* // Import for Scanner, Queue, and LinkedList
import kotlin.math.max // Import for max function

private const val MAX_L = 70

private var R = 0
private var C = 0
private var K = 0 // 행, 열, 골렘의 개수를 의미합니다
private val A = Array(MAX_L + 3) { IntArray(MAX_L) } // 실제 숲을 [3~R+2][0~C-1]로 사용하기위해 행은 3만큼의 크기를 더 갖습니다
private val dy = intArrayOf(-1, 0, 1, 0)
private val dx = intArrayOf(0, 1, 0, -1)
private val isExit = Array(MAX_L + 3) {
    BooleanArray(MAX_L)
} // 해당 칸이 골렘의 출구인지 저장합니다
private var answer = 0 // 각 정령들이 도달할 수 있는 최하단 행의 총합을 저장합니다

fun main() {
    val scanner = Scanner(System.`in`)
    R = scanner.nextInt()
    C = scanner.nextInt()
    K = scanner.nextInt()
    for (id in 1..K) { // 골렘 번호 id
        val x = scanner.nextInt() - 1
        val d = scanner.nextInt()
        down(0, x, d, id)
    }
    println(answer)
}

// 골렘id가 중심 (y, x), 출구의 방향이 d일때 규칙에 따라 움직임을 취하는 함수입니다
// 1. 남쪽으로 한 칸 내려갑니다.
// 2. (1)의 방법으로 이동할 수 없으면 서쪽 방향으로 회전하면서 내려갑니다.
// 3. (1)과 (2)의 방법으로 이동할 수 없으면 동쪽 방향으로 회전하면서 내려갑니다.
private fun down(y: Int, x: Int, d: Int, id: Int) {
    if (canGo(y + 1, x)) {
        // 아래로 내려갈 수 있는 경우입니다
        down(y + 1, x, d, id)
    } else if (canGo(y + 1, x - 1)) {
        // 왼쪽 아래로 내려갈 수 있는 경우입니다
        down(y + 1, x - 1, (d + 3) % 4, id)
    } else if (canGo(y + 1, x + 1)) {
        // 오른쪽 아래로 내려갈 수 있는 경우입니다
        down(y + 1, x + 1, (d + 1) % 4, id)
    } else {
        // 1, 2, 3의 움직임을 모두 취할 수 없을때 입니다.
        if (!inRange(y - 1, x - 1) || !inRange(y + 1, x + 1)) {
            // 숲을 벗어나는 경우 모든 골렘이 숲을 빠져나갑니다
            resetMap()
        } else {
            // 골렘이 숲 안에 정착합니다
            A[y][x] = id
            for (k in 0..3) A[y + dy[k]][x + dx[k]] = id
            // 골렘의 출구를 기록하고
            isExit[y + dy[d]][x + dx[d]] = true
            // bfs를 통해 정령이 최대로 내려갈 수 있는 행를 계산하여 누적합합니다
            answer += bfs(y, x) - 3 + 1
        }
    }
}

// (y, x)가 숲의 범위 안에 있는지 확인하는 함수입니다
private fun inRange(y: Int, x: Int): Boolean {
    return 3 <= y && y < R + 3 && 0 <= x && x < C
}

// 숲에 있는 골렘들이 모두 빠져나갑니다
private fun resetMap() {
    for (i in 0 until R + 3) {
        for (j in 0 until C) {
            A[i][j] = 0
            isExit[i][j] = false
        }
    }
}

// 골렘의 중심이 y, x에 위치할 수 있는지 확인합니다.
// 북쪽에서 남쪽으로 내려와야하므로 중심이 (y, x)에 위치할때의 범위와 (y-1, x)에 위치할때의 범위 모두 확인합니다
private fun canGo(y: Int, x: Int): Boolean {
    var flag = 0 <= x - 1 && x + 1 < C && y + 1 < R + 3
    flag = flag && A[y - 1][x - 1] == 0
    flag = flag && A[y - 1][x] == 0
    flag = flag && A[y - 1][x + 1] == 0
    flag = flag && A[y][x - 1] == 0
    flag = flag && A[y][x] == 0
    flag = flag && A[y][x + 1] == 0
    flag = flag && A[y + 1][x] == 0
    return flag
}

// 정령이 움직일 수 있는 모든 범위를 확인하고 도달할 수 있는 최하단 행을 반환합니다
private fun bfs(y: Int, x: Int): Int {
    var result = y
    val q: Queue<IntArray> = LinkedList()
    val visit = Array(MAX_L + 3) {
        BooleanArray(MAX_L)
    }
    q.offer(intArrayOf(y, x))
    visit[y][x] = true
    while (q.isNotEmpty()) {
        val cur = q.poll()
        for (k in 0..3) {
            val ny = cur[0] + dy[k]
            val nx = cur[1] + dx[k]
            // 정령의 움직임은 골렘 내부이거나
            // 골렘의 탈출구에 위치하고 있다면 다른 골렘으로 옮겨 갈 수 있습니다
            if (inRange(ny, nx) && !visit[ny][nx] && (A[ny][nx] == A[cur[0]][cur[1]] || A[ny][nx] != 0 && isExit[cur[0]][cur[1]])) {
                q.offer(intArrayOf(ny, nx))
                visit[ny][nx] = true
                result = max(result.toDouble(), ny.toDouble()).toInt()
            }
        }
    }
    return result
}