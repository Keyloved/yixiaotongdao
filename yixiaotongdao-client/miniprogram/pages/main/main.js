
//获取应用实例
const app = getApp()
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  data: {
    hasUserInfo: false,
    canIUse: false,
    intervarID: '',
    userId: '',
    teamingNeedsNum:-1,
  },
  
  /**
   * 每五秒刷新一次
   */
  reflush:function(e) {
    //检查用户登录是否过期
    //utils.checksession();

    if (app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请到【我的】登录',
        success: function (res) {
          if (res.confirm) {
            wx.switchTab({
              url: '../userPage/userPage',
            })
          }
        }
      })
    }
    else {
      this.getTeamingNeeds()
    }
  },
  

/**
 * 进入创建组队页面
 */
  enterAddNew:function(e){
    //检查用户登录是否过期
    //utils.checksession();

    if (app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请到【我的】登录',
        success: function (res) {
          if (res.confirm) {
            wx.switchTab({
              url: '../userPage/userPage',
            })
          }
        }
      })
    }
    else {
      var competitionName = e.currentTarget.dataset.competitionname
      console.log("competitionName:" + competitionName)
      if (competitionName != undefined){
        wx.showModal({
          title: '',
          content:'在类型中选择【学习】 -- > 然后右侧选择【竞赛】',
          duration: 5000,
          success:function(res){
            if(res.confirm) {
              wx.navigateTo({
                url: '../addNew/addNew',
              })
            }
          }
        })    
      }
      else {
        wx.navigateTo({
          url: '../addNew/addNew',
        })
      }
    }
  },

/**
 * 进入查看需求列表页面
 */
  getAllMyList: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    var that = this
    console.log("main页面userId:" + that.data.userId)
    wx.navigateTo({
      url: '../needsManager/needsManager?userId=' + app.globalData.userId + '&whichOperation=' + JSON.stringify(e.currentTarget.dataset.whichoperation),
    })
  },

/**
 * 搜索
 */
  suo: function (e) {
    //var searchContent = e.detail.value.searchContent
    wx.navigateTo({
      url: '../search/search'
    })
  },

/**
 * 进入某个分类
 */
  classificationDetails:function(e) {
      var that = this
      console.log("userId:"+that.data.userId)
      if (that.data.userId == null) {
        that.setData({
          userId:app.globalData.userId
        })
      }
      wx.navigateTo({
        url: '../classificationDetails/classificationDetails?kindId=' + JSON.stringify(e.currentTarget.dataset.kindid),
      })
      
  },
  
  onLoad: function () {
    console.log("main页面userId:" + app.globalData.userId)
    let that = this;
    that.setData({
      userId: app.globalData.userId,
    })
  },

  onReady: function () {
    this.getTeamingNeeds()
  },

  onShow:function() {
    this.getTeamingNeeds()
  },

  getTeamingNeeds:function(e){
    var that = this
    
    wx.request({
      url:app.globalData.host +'/needsManagement/getTeamingNeeds?userId=' + app.globalData.userId, //仅为示例，并非真实的接口地址
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          console.log("获取用户组队需求列表成功");
          var array = res.data.data
          console.log("正在组队需求数量:" + array.length)
          that.setData({
            teamingNeedsNum: array.length
          })
        } 
      }
    })
  },

})