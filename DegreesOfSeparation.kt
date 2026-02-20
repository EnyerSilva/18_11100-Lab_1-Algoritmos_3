import java.io.File //Libreria para trabajar con archivos en Kotlin
import java.util.* //Libreria que permite usar las propiedades de una cola

/**
 * Inicio del programa.
 * Se encarga de la lectura de archivos, construcción del grafo y ejecución del cálculo.
 */

fun main(args: Array<String>) {
    // 1. Validar que se reciban exactamente dos nombres como argumentos por consola
    if (args.size < 2) {
        println("Uso: programa <personaA> <personaB>")
        return
    }

    val personaA = args[0]
    val personaB = args[1]

    // Inicialización del grafo basado en listas de adyacencia
    val grafo = ListaAdyacenciaGrafo<String>()
    val archivo = File("input.txt") 

    // 2. Cargar el grafo desde el archivo input.txt
    if (archivo.exists()) {
        archivo.forEachLine { linea ->
            // Se asume que cada línea contiene dos nombres separados por espacio (una relación)
            val nombres = linea.split(" ") 
            if (nombres.size == 2) {
                val u = nombres[0]
                val v = nombres[1]
                
                // Asegurar que ambos nodos existan en el grafo
                grafo.agregarVertice(u)
                grafo.agregarVertice(v)
                
                // Se crean arcos en ambos sentidos (Grafo No Dirigido)
                grafo.conectar(u, v)
                grafo.conectar(v, u)
            }
        }
    } else {
        println("Error: El archivo 'input.txt' no fue encontrado.") //Aseguramos que solo leamos este archivo
        return
    }

    // 3. Calcular e imprimir el grado de separación utilizando BFS
    val resultado = grafo.calcularGrado(personaA, personaB)
    
    when (resultado) {
        -1 -> println(-1)
        0 -> println(0)
        else -> println(resultado)
    }
}

// CLASE E INTERFAZ DEL GRAFO

// Interfaz que define las operaciones básicas que necesitamos de un grafo en este caso.
interface Grafo<T> {
    fun agregarVertice(v: T): Boolean
    fun conectar(desde: T, hasta: T): Boolean
    fun contiene(v: T): Boolean
    fun obtenerArcosSalida(v: T): List<T>
}

/**
 * Implementación de Grafo usando lista de adyacencia.
 */
class ListaAdyacenciaGrafo<T> : Grafo<T> {
    private val adyacencias: MutableMap<T, MutableList<T>> = mutableMapOf()

    override fun contiene(v: T): Boolean = v in adyacencias 

    override fun agregarVertice(v: T): Boolean { 
        if (v !in adyacencias) {
            adyacencias[v] = mutableListOf() 
            return true
        }
        return false 
    }

    override fun conectar(desde: T, hasta: T): Boolean { 
        // Validar existencia de nodos y evitar arcos repetidos
        if (desde !in adyacencias || hasta !in adyacencias) return false 
        if (hasta in adyacencias[desde]!!) return false
        
        adyacencias[desde]!!.add(hasta)
        return true
    }

    override fun obtenerArcosSalida(v: T): List<T> { 
        return adyacencias[v] ?: listOf()
    }

    /**
     * Implementación de BFS para hallar la distancia más corta.
     * Retorna el número de saltos entre inicio y objetivo, o -1 si no hay camino.
     */
    fun calcularGrado(inicio: T, objetivo: T): Int {
        // Casos base: identidad y existencia
        if (inicio == objetivo) return 0 
        if (!contiene(inicio) || !contiene(objetivo)) return -1 

        // Registro de distancias para evitar ciclos y marcar nodos visitados
        val distancias = mutableMapOf<T, Int>()
        val cola: Queue<T> = LinkedList<T>()

        // Configuración inicial
        cola.add(inicio)
        distancias[inicio] = 0

        while (cola.isNotEmpty()) {
            val actual = cola.poll()
            val dActual = distancias[actual]!!

            // Explorar vecinos (nivel por nivel)
            for (sucesor in obtenerArcosSalida(actual)) {
                if (sucesor !in distancias) {
                    distancias[sucesor] = dActual + 1
                    
                    // Si encontramos el objetivo, retornamos inmediatamente (es el camino más corto)
                    if (sucesor == objetivo) return distancias[sucesor]!!
                    
                    cola.add(sucesor)
                }
            }
        }
        return -1 // Si la cola se vacía y no se llegó al objetivo
    }
}
