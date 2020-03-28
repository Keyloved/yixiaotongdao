const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  /**
   * 页面的初始数据
   */
  data: {
    needsList:'',
    kindId:'',
    num: 0,
    list_index:1,
    content: '选择左侧任意项加载内容',
    kinds: [
       //日常分类内容
      {
        items: [
          {
            id: '0',
            text: "租房子"
          },
          {
            id: '1',
            text: '社团'
          },
          {
            id: '2',
            text: '聊天'
          },
          {
            id: '3',
            text: '外卖拼单'
          }, {
            id: '4',
            text: '购物'
          },
          {
            id: '5',
            text: '其他'
          }
        ]
      },
      //娱乐分类内容
      {
        items: [
          {
            id: '0',
            text: "K歌"
          },
          {
            id: '1',
            text: '听音乐'
          },
          {
            id: '2',
            text: '电影'
          },
          {
            id: '3',
            text: '刷剧'
          }, {
            id: '4',
            text: '演唱会'
          },
          {
            id: '5',
            text:'游戏'
          },
          {
            id: '6',
            text: '其他'
          }
        ]
      },
      //驾校分类内容
      {
        items: [
          {
            id: '0',
            text: "驾校"
          }
        ]
      },

       //旅游分类内容
      {
        items: [
          {
            id: '0',
            text: "旅游"
          }
        ]
      }, 
      //拼车分类内容
      {
        items: [
          {
            id: '0',
            text: "拼车"
          }
        ]
      },
      //学习分类内容
      {
        items: [
          {
            id: '0',
            text: "自习"
          },
          {
            id: '1',
            text: '竞赛'
          },
          {
            id: '2',
            text: '选修课'
          },
          {
            id: '3',
            text: '讲座'
          }, {
            id: '4',
            text: '考研'
          },
          {
            id: '5',
            text: '其他'
          }
        ]
      },
      //运动分类内容
      {
        items: [
          {
            id: '0',
            text: "比赛"
          },
          {
            id: '1',
            text: '跑步'
          },
          {
            id: '2',
            text: '打球'
          },
          {
            id: '3',
            text: '健身'
          }, 
          {
            id: '4',
            text: '其他'
          }
        ]
      },
      //其他分类内容
      {
        items: [
          {
            id: '0',
            text: "其他"
          }
        ]
      }
    ]
  },
  clickList: function (e) {
    var that = this;
    console.log(e)
    let num = e.target.id
    let list_index = Number(num) + 1
    console.log(num)
    let content = this.data.kinds[this.data.kindId].items[num].text
    //console.log(content.text)
    that.setData({
      num: num,
      list_index: list_index
    })
    console.log(that.data.num)
  },

  /**
   * 进入用户资料页面
   */
  enterUserInfo:function(e) {
    console.log("senderId:" + e.currentTarget.dataset.senderid)
    wx.navigateTo({
      url: '../userInfo/userInfo?senderId=' + JSON.stringify(e.currentTarget.dataset.senderid),
    })
  },
  
  /**
   * 进入需求详细页面
   */
  enterNeedDetails:function(e){
    console.log(e.currentTarget.dataset.item)
    wx.navigateTo({
      url: '../needsDetails/needsDetails?data=' + JSON.stringify(e.currentTarget.dataset.item),
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      var that = this;
      var kindId = JSON.parse(options.kindId);
      that.setData({
        kindId: kindId
      })
      that.getNeedInfoByNeedTypeId();
  },

  getNeedInfoByNeedTypeId: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    console.log("kindId"+this.data.kindId);
    wx.request({
      url:app.globalData.host +'/needsManagement/getNeedInfoByNeedTypeId?needTypeId=' + this.data.kindId, //仅为示例，并非真实的接口地址
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          var that = this;
          console.log("获取数据成功");
          var array = res.data.data;
          if (array != null) {
          console.log(array);
          that.setData({
            needsList: array,
          })
          }
          else {
            wx.showToast({
              title: '数据空',
            })
          }
          
        } else {
          console.log("获取需求信息失败");
          wx.showToast({
            title: '系统出错',
            duration:500,
            icon:'none'
          })
        }
      },
      fail:function(res) {
        wx.showToast({
          title: '与服务器失联啦',
          icon:'none'
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
  onShow() { //返回显示页面状态函数
    var that = this
    that.getNeedInfoByNeedTypeId();
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
      that.getNeedInfoByNeedTypeId()
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