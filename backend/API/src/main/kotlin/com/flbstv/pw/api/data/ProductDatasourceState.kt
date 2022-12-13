package com.flbstv.pw.api.data

import com.flbstv.pw.api.const.ProductDatasourceStatus
import java.time.LocalDateTime

data class ProductDatasourceState(val id: Int, val name: String, val status: ProductDatasourceStatus, val lastRun: LocalDateTime)