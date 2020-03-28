// miniprogram/pages/collections/collections.js
const app = getApp()
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    collections:null,
    userId:''
  },

  /**
   * 删除收藏操作
   */
  deleteColletion:function(e) {
    //检查用户登录是否过期
    //utils.checksession();

    var that = this
    wx.request({
      url:app.globalData.host +'/collection/deleteCollection?collectionId=' + e.currentTarget.dataset.collectionid, //仅为示例，并非真实的接口地址
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          console.log("删除收藏成功");
          wx.showToast({
            title: '删除收藏成功',
            icon: 'success',
          })
          that.getCollections()
        } else {
          console.log("删除收藏失败");
          wx.showToast({
            title: '删除收藏失败',
            icon: 'none',
          })
        }
      }
    })
  },

  /**
   * 进入需求详情页
   */
  detailsNeeds: function (e) {
    wx.navigateTo({
      url: '../needsDetails/needsDetails?data=' + JSON.stringify(e.currentTarget.dataset.item),
    })
  },

  /**
   * 进入用户详情页面
   */
  enterUserInfo: function (e) {
    var that = this;
    wx.navigateTo({
      url: '../userInfo/userInfo?senderId=' + JSON.stringify(e.currentTarget.dataset.userid),
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this;
    that.setData({
      userId: app.globalData.userId
    });
    //获取收藏列表在onShow方法中
    //that.getCollections()
  },

  /**
   * 获取收藏列表
   */
  getCollections:function(e) {
    //检查用户登录是否过期
    //utils.checksession();

    wx.request({
      url:app.globalData.host +'/collection/getCollections?userId=' + this.data.userId, //仅为示例，并非真实的接口地址
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          var that = this
          var collections = res.data.data
          if (collections.length == 0 || collections == '') {
            console.log("collections:" + collections)
            that.setData({
              collections: collections
            })
            wx.showToast({
              title: '空空如也～～',
              icon: 'none',
              duration:1000
            })
          }
          else {
            that.setData({
              collections: collections
            })
          }
        } else {
          console.log("获取收藏失败");
          wx.showToast({
            title: '获取收藏失败',
            icon:'none',
          })
        }
      }
    })
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
    var that  = this
    that.getCollections()
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
    var that = this
    wx.showNavigationBarLoading()
    setTimeout(function () {
      //获取收藏列表
      that.getCollections()
      wx.hideNavigationBarLoading()
      wx.stopPullDownRefresh()
    }, 1500);
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