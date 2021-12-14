const userAjaxUrl = "admin/menusList/";
const timeVotingUrl = "voting/set_time"
const getVotingTimeUrl = "voting/voting_time"

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl,
    updateTable: function () {
        $.get(userAjaxUrl, updateTableByData);
    }
}

function renderEditBtn(data, type, row) {
    if (type === "display") {
        return "<a href=menus?date=" + row.date + "><span class='fa fa-pencil'></span></a>";
    }
}

function renderDeleteBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='deleteRow(`" + row.date + "`);'><span class='fa fa-remove'></span></a>";
    }
}

function renderCopyBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='copyMenu(`" + row.date + "`)'><span class='fa fa-copy'></span></a>"
    }
}

function copyMenu(date) {
    $.ajax({
        url: userAjaxUrl,
        type: "POST",
        data: {date: date}
    }).done(function () {
        ctx.updateTable();
        successNoty("common.copied");
    });
}

function deleteRow(date) {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            url: ctx.ajaxUrl,
            type: "DELETE",
            data: {date: date}
        }).done(function () {
            ctx.updateTable();
            successNoty("common.deleted");
        });
    }
}

function setTime() {
    $.post({
            url: timeVotingUrl,
            data: {time: $('#time').val()}
        }
    ).done(function () {
        successNoty("voting.time_enabled");
    });
}

function createMenu() {
    let date = $('#date').val();
    $('#dateform').attr('action', 'menus?' + date)
}

$(document).ready(function () {
    $.fn.dataTable.moment('DD.MM.YYYY');
    makeEditable(
        {
            "columns": [
                {
                    "data": "restaurants",
                    "render": function (data, type, row) {
                        let ro = '';
                        $.each(data, function (key, value) {
                            if (key === (data.length - 1)) {
                                ro += ("<a href=dishes/" + value.id + "?date=" + row.date + ">" + value.name + "</a>");
                            } else {
                                ro += ("<a href=dishes/" + value.id + "?date=" + row.date + ">" + value.name + "</a>" + ', ');
                            }
                        });
                        return ro;
                    }
                },
                {
                    "data":
                        "date"
                }
                ,
                {
                    "orderable":
                        false,
                    "defaultContent":
                        "",
                    "render":
                    renderEditBtn
                }
                ,
                {
                    "orderable":
                        false,
                    "defaultContent":
                        "",
                    "render":
                    renderDeleteBtn
                }
                ,
                {
                    "orderable":
                        false,
                    "defaultContent":
                        "",
                    "render":
                    renderCopyBtn
                }
            ],
            "order":
                [
                    [
                        1,
                        "desc"
                    ]
                ]
        }
    );
    var date_format = (dateFormat === "ru_date_format" ? 'd.m.Y' : 'Y-m-d');
    $.datetimepicker.setLocale(localeCode);
    $('#date').datetimepicker({
        timepicker: false,
        format: date_format,
        value: new Date().toISOString().substring(0, 10)
    });

    $.get(getVotingTimeUrl, function (data) {
        $('#time').datetimepicker({
            datepicker: false,
            format: 'H:i',
            value: data
        })
    });
})
;