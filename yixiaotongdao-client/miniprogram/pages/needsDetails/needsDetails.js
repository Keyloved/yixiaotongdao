// miniprogram/pages/needsDetails/needsDetails.js
const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
      item:{},
      userId: '',
      evaluateList:{},
      needsId:'',
      needsHasUser:false
  },

  /**
   * 进入人员管理页面
   */
  enterJoinedUserManager:function(e){
      console.log("needsId:" + this.data.needsId)
      wx.navigateTo({
        url: '../joinedUserManager/joinedUserManager?needsId=' + JSON.stringify(this.data.needsId),
      })
  },

  /**
   * 分享
   */
  onShareAppMessage: function (res) {
    if (res.from === 'button') {
    }
    return {
      title: '一校同道',
      path: '/pages/needsDetails/needsDetails',
      success: function (res) {
        console.log('成功', res)
      }
    }
  },

  /**
  * 进入用户详情页面
  */
  enterUserInfo: function (e) {
    var that = this;
    console.log("userid:" + e.currentTarget.dataset.senderid)
    wx.navigateTo({
      url: '../userInfo/userInfo?senderId=' + JSON.stringify(e.currentTarget.dataset.senderid),
    })
  },

  /**
   * 收藏
   */
  collection:function(e) {
    //检查用户登录是否过期
    //utils.checksession();

    if (this.data.userId == null || app.globalData.userId == null) {
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
        wx.request({
          url: app.globalData.host +'/collection/addCollection',
          data: { "userId": this.data.userId, "needsId": this.data.needsId },
          method: 'POST',
          header: {
            'content-type': 'application/json', // 默认值
            'token': app.globalData.token
          },
          success: (res) => {
            if (res.data.isSuccess) {
              wx.showToast({
                title: '收藏成功',
                icon: 'success',
                duration: 500
              })
            }
            else {
              var message = res.data.message
              console.log("message:" + message)
              wx.showToast({
                title: '收藏过啦',
                icon: 'none',
                duration: 500
              })
            }
          },
          fail: function (res) {
            wx.showToast({
              title: '与服务器失联啦',
              icon: 'none',
              duration: 1000
            })
          }
        })
    }
  },

  /**
   * 联系ta
   */
    enterCommunicate:function(e) {
        wx.showModal({
          title: '联系ta',
          content: '请通过qq或者微信联系，谢谢',
          showCancel: false
        })
    },

    /**
     * 人员已满
     */
    overNum:function(e) {
      wx.showModal({
        title: '人员已满',
        content: '人员已满，请通过qq或者微信联系，谢谢',
        showCancel:false
      })
    },

    /**
     * 已过期
     */
    overTime: function (e) {
      wx.showModal({
        title: '已过期',
        content: '已过期，请通过qq或者微信联系，谢谢',
        showCancel: false
      })
    },

    /**
     * 申请/评论/回复
     */
    evaluate:function(e) {
      console.log(e.currentTarget.dataset.categoryId)
      if(this.data.userId == null || app.globalData.userId == null) {
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
        //检查是否是用户自己本人要评论或者回复
        if (e.currentTarget.dataset.item.senderId != app.globalData.userId) {
          wx.navigateTo({
            url: '../evaluatePage/evaluate?data=' + JSON.stringify(e.currentTarget.dataset.item) + '&categoryId=' + JSON.stringify(e.currentTarget.dataset.id) + '&whichOperation=' + JSON.stringify(e.currentTarget.dataset.whichoperation)
          })  
        }
      }
    },

    /**
     * 编辑
     */
    editNeeds: function (e) {
      wx.navigateTo({
        url: '../editNeeds/editNeeds?needId=' + JSON.stringify(e.currentTarget.dataset.id),
      })
    },

    /**
     * 删除
     */
    deleteNeeds: function (e) {
      //检查用户登录是否过期
      //utils.checksession();
      
      wx.showModal({
        title: '删除提示',
        content: '确认删除吗？',
        success: function (res) {
          if (res.confirm) {
            wx.request({
              url: app.globalData.host +'/needsManagement/deleteNeeds/' + e.currentTarget.dataset.id, //仅为示例，并非真实的接口地址
              method: 'GET',
              header: {
                'content-type': 'application/json', // 默认值
                'token': app.globalData.token
              },
              success: (res) => {
                if (res.data.isSuccess) {
                  console.log("删除需求信息成功");
                  wx.showToast({
                    title: '删除成功',
                    icon:'success'
                  })
                  wx.navigateBack({
                    delta: -1
                  })
                } else {
                  console.log("删除失败");
                  wx.showToast({
                    title: '删除失败',
                    icon: 'none',
                    duration: 500
                  })
                }
              },
              fail: function (res) {
                wx.showToast({
                  title: '与服务器失联啦',
                  icon: 'none',
                  duration: 1000
                })
              }
            })
          }
        },
      })

    },


    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
      let that = this;
      that.setData({
        userId: app.globalData.userId,
      })
      console.log("needsDetails页面onLoad检测userId:" + that.data.userId)
      var item = JSON.parse(options.data);
      //来自需求分类页面和消息页面的加载，两个页面传过来的数据不一致，做以下处理
      if (item.needsId == null) {
        that.setData({
          needsId:item.id
        })
      }
      else {
        that.setData({
          needsId: item.needsId
        })
      }
      //加载需求详情
      that.getNeedAndJoinUsers();
      if (that.data.item != null) {
        //加载需求的评论回复消息并且更新观看次数viewNo
        that.getAllEvaluateAndReply();
      }
      //需求被删
      else {
        wx.showToast({
          title: '需求已被删除',
          icon:'none'
        })
      }
    },

    /**
     * 获取需求详情以及需求组队队员信息
     */
  getNeedAndJoinUsers:function(e) {
    var that = this;
    console.log("needsId:" + this.data.needsId);
    wx.request({
      url: app.globalData.host +'/needsManagement/getNeedAndJoinUsers?needId=' + this.data.needsId,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          var item = res.data.data
          console.log("item" + item)
          item.needInfo.deadline = item.needInfo.deadline.substring(0, 10);
          item.needInfo.startTime = item.needInfo.startTime.substring(0, 10);

          //组队人员中检测是否有当前用户
          var joinedUsers = item.needInfo.joinUserId.split("#@#")
          var hasUser = false
          for (var index = 0; index < joinedUsers.length; ++index) {
              if (app.globalData.userId == joinedUsers[index]) {
                hasUser = true
              }
          }
           
          that.setData({
            item: item,
            needsHasUser: hasUser
          })
        }
        else {
          wx.showToast({
            title: '获取失败',
            icon: 'none'
          })
        }
      },
      fail: function (res) {
        wx.showToast({
          title: '与服务器失联啦',
          icon: 'none',
          duration: 1000
        })
      }
    })
  },

    /**
     * 获取需求所有评论回复
     */
    getAllEvaluateAndReply:function(e){
      var that = this;
      console.log("needsId:" + this.data.needsId);
      wx.request({
        url:app.globalData.host +'/MessageManage/getNeedMessageDetails?needId=' + this.data.needsId,
        method: 'GET',
        header: {
          'content-type': 'application/json', // 默认值
          'token': app.globalData.token
        },
        success: (res) => {
          if (res.data.isSuccess) {
            var evaluateList = res.data.data
            console.log("回复" + evaluateList)
            that.setData({
              evaluateList: evaluateList,
            })
          }
          else {
            wx.showToast({
              title:'获取失败',
              icon:'none'
            })
          }
        },
        fail: function (res) {
          wx.showToast({
            title: '与服务器失联啦',
            icon: 'none',
            duration: 1000
          })
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
      var that = this
      //加载需求详情
      that.getNeedAndJoinUsers();
      that.getAllEvaluateAndReply();
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
      let that = this
      wx.showNavigationBarLoading()
      setTimeout(function () {
        //加载需求详情
        that.getNeedAndJoinUsers();
        if (that.data.item != null) {
          //加载需求的评论回复消息并且更新观看次数viewNo
          that.getAllEvaluateAndReply();
        }
        //需求被删
        else {
          wx.showToast({
            title: '需求已被删除',
            icon: 'none'
          })
        }
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