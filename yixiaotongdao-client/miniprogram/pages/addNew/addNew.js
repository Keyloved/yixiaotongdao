// miniprogram/pages/editUser/editUserDetails.js
const app = getApp();
// 在需要使用的js文件中，导入js
var utils = require('/../utils/utils.js');
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  /**
   * 页面的初始数据
   */

  data: {
    userId: '',
    // 字数限制
    current: 0,
    max: 300,
    startDate: '',
    deadline:'',
    dataFromMyList: {},
    index1:0,
    items: [
      { name: '是', checked: true },
      { name: '否', checked: false }
    ],
    userJoined:1,
    show: false,//控制下拉列表的显示隐藏，false隐藏、true显示
    selectData: ['日常', '娱乐', '驾校', '旅游', '拼车', '学习', '运动', '其他'],//下拉列表的数据
    //日常分类内容
    //0: ["租房子", '社团', '聊天', '外卖拼单', '购物', '其他'],
    //娱乐分类内容
    //1: ["K歌", '听音乐', '电影', '刷剧', '演唱会', '游戏'],
    //2: ["驾校"],
    //3: ["旅游"],
    //4: ["拼车"],
    //学习分类内容
    //5: ["自习", '社团', '聊天', '外卖拼单', '购物', '其他'],
    //运动分类内容
    //6: ["比赛", "跑步", "约球", "健身"],
    //其他分类内容
    //7: ["其他"],

    kinds: [
      //日常分类内容
      {
        items: [
          "租房子", '社团', '聊天', '外卖拼单', '购物', '其他']
      },
      //娱乐分类内容
      {
        items: [
          "K歌", '听音乐', '电影', '刷剧', '演唱会', '游戏', '其他']
      },
      //驾校分类内容
      {
        items: ['驾校',]
      },

      //旅游分类内容
      {
        items: ['旅游',]
      },
      //拼车分类内容
      {
        items: ['拼车']
      },
      //学习分类内容
      {
        items: ['讲座','自习','竞赛','选修课','考研','其他']
      },
      //运动分类内容
      {
        items: ['比赛','跑步','打球','健身','其他']
      },
      //其他分类内容
      {
        items: [ "其他"]
      }
    ],
    
    type_index: 0//选择的下拉列表下标
  },

/**
 * 下拉框设置
 */
  // 点击下拉显示框

  selectTap() {

    this.setData({

      show: !this.data.show

    });

  },

  // 点击下拉列表

  optionTap(e) {

    let Index = e.currentTarget.dataset.index;//获取点击的下拉列表的下标

    this.setData({

      type_index: Index,
      index1:0,
      show: !this.data.show

    });

  },

  /**
   * 个人是否参加
   */
  changeState:function(e){
    var that = this;
    console.log(e.detail.value);
    if (e.detail.value == '是')
    {
      that.setData({
        userJoined: 1
      })
    }
    else {
      that.setData({
        userJoined: 0
      })
    }
    console.log(that.data.userJoined);
  },

  // 文本框字数限制
  limit: function (e) {
    var value = e.detail.value;
    var length = parseInt(value.length);

    if (length > this.data.noteMaxLen) {
      return;
    }

    this.setData({
      current: length
    });
  },

  listenerPickerSelected: function (e) {
    //改变index值，通过setData()方法重绘界面
    this.setData({
      index1: e.detail.value
    });
  },

  /**
   * 监听日期picker选择器
   */
  listenerStartDatePickerSelected: function (e) {
    this.setData({
      startDate: e.detail.value
    })
  },

  /**
   * 监听日期picker选择器
   */
  listenerDeadlinePickerSelected: function (e) {
    this.setData({
      deadline: e.detail.value
    })
  },

  switchChange: function (e) {
    if (e.detail.value) {
      this.setData({ sex: '男' });
    } else {
      this.setData({ sex: '女' });
    }
  },

  formSubmit: function (e) {
    //检查用户登录是否过期
    //utils.checksession();
    
    if (e.detail.value.title.length == 0) {
      wx.showToast({
        title: '标题不能为空',
        icon: 'none',
      })
    } else if (e.detail.value.content.length == 0) {
      wx.showToast({
        title: '需求内容不能为空',
        icon: 'none',
      })
    } else if (e.detail.value.limitNo.length == 0) {
      wx.showToast({
        title: '人数限制不能为空',
        icon: 'none',
      })
    } else if (e.detail.value.qq.length == 0 && e.detail.value.weChat.length == 0) {
      wx.showToast({
        title: 'qq,微信至少填写一个',
        icon: 'none',
      })
    } else if (e.detail.value.phoneNo.length != 0 && e.detail.value.phoneNo.length != 11){
      wx.showToast({
        title: '手机号有误',
        icon: 'none',
      })
    }
    else {
      var data = { "needsTypeId": this.data.type_index,"categoryId": this.data.index1, "userId": this.data.userId, "title": e.detail.value.title, "content": e.detail.value.content, "startTime": e.detail.value.startTime, "deadline": e.detail.value.deadline, "qq": e.detail.value.qq, "weChat": e.detail.value.weChat, "limitNo": e.detail.value.limitNo, "userJoined": this.data.userJoined };
        wx.showModal({
          title: '',
          content: '确定添加吗？',
          icon: 'success',
          duration: 2000,
          success:function(res) {
            if(res.confirm) {
              wx.request({
                url:app.globalData.host+'/needsManagement/addNeeds',
                method: 'POST',
                data: JSON.stringify(data),
                header: {
                  'content-type': 'application/json', // 默认值
                  'token': app.globalData.token
                },
                success: (res) => {
                  if (res.data.isSuccess) {
                    wx.showModal({
                      title: 'success',
                      content: '添加需求成功',
                      icon: 'success',
                      showCancel:false,
                      success:function(res) {
                        if (res.confirm) {
                          //返回上一页
                          wx.navigateBack({
                            delta: -1
                          })
                        }
                      }
                    })
                    console.log("新增需求信息成功");
                  
                  }
                  else {
                    wx.showModal({
                      title: 'fail',
                      content: '添加需求失败',
                      icon: 'none',
                      showCancel: false,
                      success: function (res) {
                        if (res.confirm) {
                          //返回上一页
                          wx.navigateBack({
                            delta: -1
                          })
                        }
                      }
                    })
                  }
                },
                fail: function (res) {
                  wx.showToast({
                    title: '失败',
                    duration: 500,
                  })
                }
              })
            }
          },
          
        })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    // 调用函数时，传入new Date()参数，返回值是日期和时间
    var time = utils.formatTime(new Date());
    that.setData({
      userId: app.globalData.userId,
      startDate:time,
      deadline:time,
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