import java.io.IOException

sealed class ApiResult<out T : Any> {
    data class Success<out T : Any>(val value: T) : ApiResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>()
}

fun <T : Any> tryResource(resource: () -> ApiResult<T>): ApiResult<T> {
    return try {
        resource()
    } catch (e: IOException) {
        ApiResult.Error(e.message ?: "", e.cause)
    }
}

class Product(val productName: String)
class Customer(val customerName: String)

class StoreApi {
    fun getProducts(): ApiResult<Product> {
        return ApiResult.Success(Product("example product"))
    }

    fun getCustomers(): ApiResult<Customer> {
        return ApiResult.Success(Customer("example customer"))
    }
}

class StoreRepository(private val api: StoreApi) {

    fun getStoreDetails() {
        val networkProducts = tryResource { api.getProducts() }
        when (networkProducts) {
            is ApiResult.Success -> println("products is: ${networkProducts.value.productName}")
            is ApiResult.Error -> println("could not get products")
        }

        val networkCustomers = tryResource { api.getCustomers() }
        when (networkCustomers) {
            is ApiResult.Success -> println("customer is: ${networkCustomers.value.customerName}")
            is ApiResult.Error -> println("could not get products")
        }
    }
}
