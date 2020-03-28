const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  data: {
    whichOperation:'',
    row1: 300,
    userId: '',
    isAll: 0,
    category: ['拼车', '竞赛', '电影', '学习', '其他'],
    length: {},
    threeArray: '', //模拟将后台获取到的数组对象数据按照一行3个的单元数据的格式切割成新的数组对象（简单的说：比如获取到数组是9个元素，切分成，3个元素一组的子数组）
  },

  /**
   * 申请推出组队
   */
  applyExitNeed:function(e) {
    wx.navigateTo({
      url: '../evaluatePage/evaluate?data=' + JSON.stringify(e.currentTarget.dataset.item) + '&categoryId=' + JSON.stringify(e.currentTarget.dataset.id) + '&whichOperation=' + JSON.stringify(e.currentTarget.dataset.whichoperation)
      })  
  },

  detailsNeeds:function(e) {
    wx.navigateTo({
      url: '../needsDetails/needsDetails?data=' + JSON.stringify(e.currentTarget.dataset.item),
    })
  },
  
  editNeeds: function (e) {
    wx.navigateTo({
      url: '../editNeeds/editNeeds'
    })
  },

  deleteNeeds: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    wx.showModal({
      title: '',
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
                wx.showModal({
                  title: '',
                  content: '删除成功',
                  showCancel: false,
                  confirmColor: '#333ccc',
                  duration: 1000,
                  success: function (res) {
                    if (res.confirm) {
                      wx.request({
                        url: app.globalData.host +'/needsManagement/getNeedsByUserId?userId=5', //仅为示例，并非真实的接口地址
                        method: 'GET',
                        header: {
                          'content-type': 'application/json', // 默认值
                          'token': app.globalData.token
                        },
                        success: (ress) => {
                          if (ress.data.isSuccess) {
                            console.log("获取用户需求信息成功");
                            wx.redirectTo({
                              url: '../user/userNeedsManager?data=' + JSON.stringify(ress.data.data),    //?data=' + JSON.stringify(res.data),
                            })
                          }
                        }
                      })
                    }
                  }
                })

              } else {
                console.log("删除需求信息失败");
                wx.showToast({
                  title: '失败',
                  icon: 'fail',
                  duration: 2000
                })
              }
            }
          })
        }
      }
    })

  },

  flush: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    wx.request({
      url: app.globalData.host +'/needsManagement/getNeedsByUserId?userId=5', //仅为示例，并非真实的接口地址
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          console.log("获取用户需求信息成功");
          wx.navigateTo({
            url: '../user/userNeedsManager?data=' + JSON.stringify(res.data.data),    //?data=' + JSON.stringify(res.data),
          })
        } else {
          console.log("获取用户需求信息失败");
          wx.redirectTo({
            url: '../user/userNeedsManage'
          })
        }
      }
    })
  },

  /**
   * 页面加载
   */
  onLoad: function (options) {
    let that = this;
    var userId = JSON.parse(options.userId)
    if (userId.length != 0) {
      that.setData({
        userId: userId,
        whichOperation: JSON.parse(options.whichOperation)
      })
    }  
    console.log("whichOperation:"+that.data.whichOperation)

    //加载需求列表在onShow方法中
    //that.getAllMyList();
  },

  /**
   * 获取所有列表
   */
  getAllMyList:function(e) {
    //检查用户登录是否过期
    //utils.checksession();
    
    var that = this
    var url = ""
    //获取用户加入的需求列表
    if (that.data.whichOperation == 'joined') {
      //url = app.globalData.host +'/needsManagement/getMyJoinedNeeds?userId=' + this.data.userId
      url = app.globalData.host + '/needsManagement/getMyJoinedNeeds?userId=' + this.data.userId
    }
    //获取正在组队的需求列表
    else if (that.data.whichOperation == 'teaming') {
      //url = app.globalData.host +'/needsManagement/getTeamingNeeds?userId=' + this.data.userId
      url = app.globalData.host + '/needsManagement/getTeamingNeeds?userId=' + this.data.userId
    }
    else {
      //获取用户创建的需求列表
      url = app.globalData.host +'/needsManagement/getNeedsByUserId?userId=' + this.data.userId
    }
    
    wx.request({
      url:url,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          console.log("获取用户需求信息成功");
          if (res.data.data.length == 0) {
            wx.showToast({
              title: '空空如也～～',
              icon:'none'
            })
          }
          else {
            let threeArray = [];
            // 使用for循环将原数据切分成新的数组
            var array = res.data.data;
            console.log(array);
            if (that.data.whichOperation == 'finished') {
              for (let i = 0, len = array.length; i < len; i += 1) {
                if (array[i].joinNo == array[i].limitNo) {
                  array[i].deadline = array[i].deadline.substring(0, 10);
                  array[i].startTime = array[i].startTime.substring(0, 10);
                  threeArray.push(array.slice(i, i + 1));
                }
              }
            }
            else if (that.data.whichOperation == 'unfinished') {
              for (let i = 0, len = array.length; i < len; i += 1) {
                if (array[i].joinNo < array[i].limitNo) {
                  array[i].deadline = array[i].deadline.substring(0, 10);
                  array[i].startTime = array[i].startTime.substring(0, 10);
                  threeArray.push(array.slice(i, i + 1));
                }
              }
            }
            else {
              for (let i = 0, len = array.length; i < len; i += 1) {
                array[i].deadline = array[i].deadline.substring(0, 10);
                array[i].startTime = array[i].startTime.substring(0, 10);
                threeArray.push(array.slice(i, i + 1));
              }
            }
            
            console.log(threeArray);
            that.setData({
              threeArray: threeArray,
              length: res.data.data.length,
            })
          }

        } else {
          console.log("获取用户需求信息失败");
          wx.showToast({
            title: '获取失败',
            icon: 'none'
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
    var that = this
    that.getAllMyList();
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
      that.getAllMyList();
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

  },
})
