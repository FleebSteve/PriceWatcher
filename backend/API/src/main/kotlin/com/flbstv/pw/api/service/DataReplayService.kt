package com.flbstv.pw.api.service

interface DataReplayService {

    fun replayProductStream()

    fun isRunning(): Boolean
}