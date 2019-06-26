package com.tifone.demo.camera.presenter

interface IPresenter<T> {
    fun requestCamera(t: T)
}