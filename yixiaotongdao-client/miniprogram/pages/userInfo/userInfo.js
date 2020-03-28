//index.js
//获取应用实例
const app = getApp()
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  data: {
    motto: '我看你的认真，小伙子',
    userInfo: {},
    userData: '',
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    userId: '',
  },
  //事件处理函数
  bindViewTap: function () {
    wx.navigateTo({
      url: '../user/userDetails',
    })
  },

  getAllMyList: function (e) {
    var that = this
    if (that.data.userInfo.isNeedsOpen == 0) {
      wx.showToast({
        title: '用户设置隐私，无法访问！',
        icon:'none',
        duration:2000
      })
    }
    else {
      wx.navigateTo({
        url: '../needsManager/needsManager?userId=' + JSON.stringify(e.currentTarget.dataset.userid) + '&whichOperation=' + JSON.stringify(e.currentTarget.dataset.whichoperation),
      })
    }
  },

  onLoad: function (options) {
    let that = this;
    if (JSON.parse(options.senderId).length != 0) {
      that.setData({
        userId: JSON.parse(options.senderId)
      }),
        that.getUserDetails()
    }
  },

  /**
   * 获取用户信息
   */
  getUserDetails: function (e) {
    //检查用户登录是否过期
    //utils.checksession();
    
    //var data = { "openId": app.globalData.openId};  
    wx.request({
      url: app.globalData.host +'/userInfo/getUserDetails?userId=' + this.data.userId, //仅为示例，并非真实的接口地址
      method: 'GET',
      //data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          this.setData({
            userInfo: res.data.data
          })
          console.log("成功获取用户数据" + res.data.data)
        } else {
          console.log("获取用户信息失败");
          wx.showToast({
            title: '获取失败',
            icon: 'none',
            duration: 1000
          })
        }
      },
      fail:function(res){
        wx.showToast({
          title: '与服务器失联啦',
          icon:'none',
          duration:1000
        })
      }
    })
  },
})
