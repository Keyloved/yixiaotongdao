// miniprogram/pages/userSetUp/userSetUp.js
const app = getApp()
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userId:'',
    isQQChecked: 1,
    isWeixinChecked: 1,
    isNeedsChecked: 1,
  },

  /**
   * 完成
   */
  finish:function(e){
    var that = this
    wx.showModal({
      title: '更改提示',
      content: '确定修改吗？',
      success:function(res) {
        if (res.confirm) {
          that.finishToBack()
        }
      }
    })
    
  },

  /**
   * 完成操作后台操作
   */
  finishToBack:function(e) {
    var that = this
    var temIsQQOpen = false, temIsWeixinOpen = false, temIsNeedsOpen = false
    if (that.data.isQQChecked == 1) {
      temIsQQOpen = true
    }
    if (that.data.isWeixinChecked == 1) {
      temIsWeixinOpen = true
    }
    if (that.data.isNeedsChecked == 1) {
      temIsNeedsOpen = true
    }
    wx.request({
      data: { "userId": that.data.userId, "isQQOpen": temIsQQOpen, "isWeixinOpen": temIsWeixinOpen, "isNeedsOpen": temIsNeedsOpen,},
      url: app.globalData.host +'/userInfo/updateUserInfoOpen', //仅为示例，并非真实的接口地址
      method: 'POST',
      //data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          wx.showToast({
            title: '更改成功',
            icon: 'success',
            duration: 2000
          })
        } else {
          console.log("更改失败");
          wx.showToast({
            title: '更改失败',
            icon: 'none',
            duration:2000
          })
          that.getUserDetails()
        }
      }
    })
  },

  /**
   * 更改用户设置
   */
  changeSwitchQQ:function(e){
    var that = this
    var isQQChecked = !that.data.isQQChecked
    that.setData({
      isQQChecked:isQQChecked
    })
  },
  changeSwitchWeixin: function(e) {
    var that = this
    var isWeixinChecked = !that.data.isWeixinChecked
    that.setData({
      isWeixinChecked: isWeixinChecked
    })
  },
  changeSwitchNeeds: function (e) {
    var that = this
    var isNeedsChecked = !that.data.isNeedsChecked
    that.setData({
      isNeedsChecked: isNeedsChecked
    })
  },

  /**
   * 获取用户信息
   */
  getUserDetails: function (e) {
    var that =this
    //var data = { "openId": app.globalData.openId};  
    wx.request({
      url: app.globalData.host +'/userInfo/getUserDetails?userId=' + that.data.userId, //仅为示例，并非真实的接口地址
      method: 'GET',
      //data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          that.setData({
            isQQChecked: res.data.data.isQQOpen,
            isWeixinChecked: res.data.data.isWeixinOpen,
            isNeedsChecked: res.data.data.isNeedsOpen
          })
        } else {
          console.log("获取用户信息失败");
          wx.showToast({
            title: '获取失败',
            icon:'none',
          })
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      var that = this
      that.setData({
        userId: app.globalData.userId
      })
      //获取用户信息在onShow方法中
      //that.getUserDetails()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    var that = this
    
    //获取用户信息
    that.getUserDetails()

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})