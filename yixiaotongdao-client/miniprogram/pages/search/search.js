const app = getApp()
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    searchValue: null,
    searchContent_size: 0,
    centent_Show: false,
    inpuVal: "",//input框内值
    listarr: [],//创建数组
    SearchText: '取消',//按钮变动值
    keydown_number: 0,//检测input框内是否有内容
    input_value: "",//value值
    hostarr: [],//热门搜索接收请求存储数组  
    name_focus: true,//获取焦点
    categoryNameLine:true,
    titleLine:false,
    contentLine:false,
    timeLine:false,
  },
  //取值input判断输入框内容修改按钮
  inputvalue: function (e) {
    if (e.detail.value != '' || e.detail.value != null) {
      this.setData({
        inputVal: e.detail.value
      })
      if (e.detail.cursor != 0) {
        this.setData({
          SearchText: "搜索",
          keydown_number: 1
        })
      } else {
        this.setData({
          SearchText: "取消",
          keydown_number: 0
        })
      }
    }
    
  },

  /**
   * 搜索类型选择
   */
  changeSearchType:function(e) {
    var that = this
    var typeName = e.currentTarget.dataset.typename;
    if (typeName == "categoryName") {
      that.setData({
        categoryNameLine: true,
        titleLine: false,
        contentLine: false,
        timeLine: false,
      })
    }
    else if (typeName == "title") {
      that.setData({
        categoryNameLine: false,
        titleLine: true,
        contentLine: false,
        timeLine: false,
      })
    }
    else if (typeName == "content") {
      that.setData({
        categoryNameLine: false,
        titleLine: false,
        contentLine: true,
        timeLine: false,
      })
    }
    else if (typeName == "time") {
      that.setData({
        categoryNameLine: false,
        titleLine: false,
        contentLine: false,
        timeLine: true,
      })
    }
  },

  //搜索方法
  search: function () {
    if (this.data.keydown_number == 1 && this.data.inputVal != '') {
      let This = this;

      //把获取的input值插入数组里面
      let arr = this.data.listarr;
      console.log(this.data.inputVal)
      console.log(this.data.input_value)
      //判断取值是手动输入还是点击赋值
      if (this.data.input_value == "") {
        // console.log('进来第er个')
        // 判断数组中是否已存在
        let arrnum = arr.indexOf(this.data.inputVal);
        console.log(arr.indexOf(this.data.inputVal));
        if (arrnum != -1) {
          // 删除已存在后重新插入至数组
          arr.splice(arrnum, 1)
          arr.unshift(this.data.inputVal);

        } else {
          arr.unshift(this.data.inputVal);
        }

      } else {
        console.log('进来第一个')
        let arr_num = arr.indexOf(this.data.input_value);
        console.log(arr.indexOf(this.data.input_value));
        if (arr_num != -1) {
          arr.splice(arr_num, 1)
          arr.unshift(this.data.input_value);
        } else {
          arr.unshift(this.data.input_value);
        }

      }
      console.log(arr)

      //存储搜索记录
      wx.setStorage({
        key: "list_arr",
        data: arr
      })

      //取出搜索记录
      wx.getStorage({
        key: 'list_arr',
        success: function (res) {
          This.setData({
            listarr: res.data
          })
        }
      })

      //从后台获取数据
      this.getNeedsInfo()

      this.setData({
        input_value: '',
        inputVal:'',
        SearchText: "取消",
        keydown_number: 0
      })
    } else {
      wx.navigateBack({
        delta:-1
      })
      console.log("取消")
    }

  },
  //清除搜索记录
  delete_list: function () {
    //清除当前数据
    this.setData({
      listarr: []
    });
    //清除缓存数据
    wx.removeStorage({
      key: 'list_arr'
    })
  },
  //点击赋值到input框
  this_value: function (e) {
    this.setData({
      name_focus: true
    })
    let value = e.currentTarget.dataset.text;
    this.setData({
      input_value: value,
      inputVal: value,
      SearchText: "搜索",
      keydown_number: 1
    })
  },

  /**
   * 获取需求详情
   */
  enterNeedInfo: function (e) {
    console.log(e.currentTarget.dataset.item)
    wx.navigateTo({
      url: '../needsDetails/needsDetails?data=' + JSON.stringify(e.currentTarget.dataset.item),
    })
  },

  /**
   * 关键字搜索
   */
  getNeedsInfo: function (e) {
    var that = this
    var keyWords = that.data.inputVal
    var typeName = ''
    if (that.data.categoryNameLine == true) {
      typeName = "category"
    }
    else if (that.data.titleLine == true) {
      typeName = "title"
    }
    else if (that.data.contentLine == true) {
      typeName = "content"
    }
    else {
      typeName = "time"
    }
    console.log(keyWords)
    wx.request({
      url: app.globalData.host + '/needsManagement/getKeyWordsContent?keyWords=' + keyWords + '&typeName=' + typeName,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        var that = this
        if (res.data.isSuccess) {
          var needsList = res.data.data
          console.log("needsList:" + needsList);
          that.setData({
            searchValue: needsList,
            searchContent_size: needsList.length,
            centent_Show: true
          })
        } else {
          console.log("获取信息失败");
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

    let This = this;
    //设置当前页标题
    wx.setNavigationBarTitle({
      title: '搜索'
    });
    //读取缓存历史搜索记录
    wx.getStorage({
      key: 'list_arr',
      success: function (res) {
        This.setData({
          listarr: res.data
        })
      }
    })
    //请求热门搜索
  },
})