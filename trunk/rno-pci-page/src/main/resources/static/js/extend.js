/**
 * Created by yang chonghui on 2016/11/22.
 */


/**
 * 定义全局对象，
 */
var bs = $.extend({}, bs);

/**
 * 添加tab页
 * @param addTabs({ id: $(this).attr("id"), title: $(this).attr('title'), close: true ,url:''});
 */
bs.addTabs = function (options) {
    if (options.id != undefined && options.id.substr(0, 13) == "parentAddtabs") {
        var url = window.location.protocol + '//' + window.location.host;
        options.url = options.url.substring(1, options.url.length); // 截取掉 /
        id = "tab_" + options.id;


        var active_flag = false;
        if ($("#" + id)) {
            active_flag = parent.$("#" + id).hasClass('active');
        }
        parent.$(".active").removeClass("active");
        //如果TAB不存在，创建一个新的TAB
        if (!parent.$("#" + id)[0]) {
            //固定TAB中IFRAME高度
            mainHeight = parent.$(document.body).height() - 200;
            //创建新TAB的title
            title = '<li id="tab_' + id + '"><a  style="margin-top:-10px" href="#' + id + '" role="tab" data-toggle="tab">' + options.title;
            // title += ' <i class="fa fa-refresh"></i>';
            //是否允许关闭
            if (options.close) {
                title += ' <i class="glyphicon glyphicon-remove" tabclose="' + id + '"></i>';

                //title += '<button type="button" class="close closeTab mlm">x</button> ';
            }
            //title +=  options.title;
            title += '</a></li>';
            //是否指定TAB内容
            if (options.content) {
                content = '<div role="tabpanel" class="tab-pane" id="' + id + '" style="height:100%">' + options.content + '</div>';
            } else {//没有内容，使用IFRAME打开链接
                content = '<div role="tabpanel" class="tab-pane active" id="' + id + '" style="height:100%;margin-left: 260px"><iframe id="iframe_' + id + '" src="' + options.url +
                    '" width="100%" seamless height="100%" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="yes" allowtransparency="yes"></iframe></div>';
            }
            //加入TABS
            parent.$(".nav-tabs").append(title);
            parent.$(".tab-content").append(content);
        } else {
            if (active_flag) {
                parent.$("#iframe_" + id).attr('src', parent.$("#iframe_" + id).attr('src'));
                //$("#iframe_" + id).attr('src', $("#tab_" + id).attr('src'));
            }
        }
        //激活TAB
        parent.$("#tab_" + id).addClass('active');
        parent.$("#" + id).addClass("active");
    } else {
        //var rand = Math.random().toString();
        //var id = rand.substring(rand.indexOf('.') + 1);
        var url = window.location.protocol + '//' + window.location.host;
        // options.url = url + options.url;
        //options.url = "/ltemr"+ options.url; //因为路径是以 / 开头， 所以 iframe 的时候少了个 /ltemr
        options.url = options.url.substring(1, options.url.length); // 截取掉 /
        id = "tab_" + options.id;
        var active_flag = false;
        if ($("#" + id)) {
            active_flag = $("#" + id).hasClass('active');
        }
        $(".active").removeClass("active");
        //如果TAB不存在，创建一个新的TAB
        if (!$("#" + id)[0]) {
            //固定TAB中IFRAME高度
            mainHeight = $(document.body).height() - 200;
            //创建新TAB的title
            title = '<li id="tab_' + id + '"><a href="#' + id + '"   style="margin-top:-10px" role="tab" data-toggle="tab">' + options.title;
            // title += ' <i class="fa fa-refresh"></i>';
            //是否允许关闭
            if (options.close) {
                title += ' <i class="glyphicon glyphicon-remove" tabclose="' + id + '"></i>';

                //title += '<button type="button" class="close closeTab mlm">x</button> ';
            }
            //title +=  options.title;
            title += '</a></li>';
            //是否指定TAB内容
            if (options.content) {
                content = '<div role="tabpanel" class="tab-pane" id="' + id + '" style="height:100%">' + options.content + '</div>';
            } else {//没有内容，使用IFRAME打开链接
                content = '<div role="tabpanel" class="tab-pane active" id="' + id + '" style="height:100%">' +
                    '<iframe id="iframe_' + id + '" src="' + options.url +
                    '" width="100%" seamless height="100%" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes"></iframe></div>';
            }
            //加入TABS
            $(".nav-tabs").append(title);
            $(".tab-content").append(content);
        } else {
            if (active_flag) {
                $("#iframe_" + id).attr('src', $("#iframe_" + id).attr('src'));
                //$("#iframe_" + id).attr('src', $("#tab_" + id).attr('src'));
            }
        }
        //激活TAB
        $("#tab_" + id).addClass('active');
        $("#" + id).addClass("active");
    }
}
//关闭tab页
var closeTab = function (id) {
    //如果关闭的是当前激活的TAB，激活他的前一个TAB
    if ($("li.active").attr('id') == "tab_" + id) {
        $("#tab_" + id).prev().addClass('active');
        $("#" + id).prev().addClass('active');
    }
    //关闭TAB
    $("#tab_" + id).remove();
    $("#" + id).remove();
};
//页面tab的关闭事件监听
$(function () {
    mainHeight = $(document.body).height() - 300;
    $('.main-left,.main-right').height(mainHeight);
    $("[addtabs]").click(function () {
        addTabs({id: $(this).attr("id"), title: $(this).attr('title'), close: true});
    });

    $(".nav-tabs").on("click", "[tabclose]", function (e) {
        id = $(this).attr("tabclose");
        closeTab(id);
    });
});


