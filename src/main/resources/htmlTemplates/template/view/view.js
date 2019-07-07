layui.use('form', function() {
    var form = layui.form;
    var entityName = "entityName-yangrui";
    var param = paramUtil.getQueryString("param");
    var uriComponent = JSON.parse(decodeURIComponent(param));
    var viewData={};
    console.log(uriComponent)
    var conditionList=[];
    for(var k in uriComponent){
        var ob={};
        ob.left=k;
        ob.right=uriComponent[k];
        conditionList.push(ob);
    }
    //从服务器获取数据
    apiClient.findAllNoPage({
        entityName:entityName,
        condition:{
            conditionList:conditionList,
            combine:"and"
        }
    },function (data) {
        if(data.length>0)
            viewData=data[0];
        //初始化vue.js的数据绑定
        var app4 = new Vue({
            el: '#viewForm',
            data: {
                viewData:viewData
            }
        })
    })
})