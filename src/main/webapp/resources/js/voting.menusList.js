const userAjaxUrl = "admin/menusList/";
const timeVotingUrl = "voting/set_time"
const timeZoneVotingUrl = "voting/time_zone"
const currentTimeUrl = "voting/current_time"
const currentTimeZoneVotingUrl = "voting/current_time_zone"
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

function setTimeZone() {
    $.post({
        url: timeZoneVotingUrl,
        data: {timeZone: $('#timeZone').val()}
    }).done(function () {
        setCurrentTime();
        successNoty("voting.time_zone_enabled");
    });
}

function setCurrentTime() {
    $.get(currentTimeUrl, function (data) {
        $('#current_time').text(data.currentTime);
    });
}

function getTimeZones() {
    $.get(timeZoneVotingUrl).done(function (data) {
        $.each(data, function (index, value) {
            $('#timeZone').append($('<option>', {html: value}));
        });
        $.get(currentTimeZoneVotingUrl).done(function (data) {
            $(`#timeZone option:contains("${data}")`).prop('selected', true);
        });
    });
}

function filterTimeZones() {
    // Получение введенного значения
    var input = $('#timeZone');
    var filter = input.value.toUpperCase();

    if (filter.length >= 3) {
        var options = input.getElementsByTagName('option');
        // Перебор элементов и скрытие/отображение на основе фильтра
        for (var i = 0; i < options.length; i++) {
            var txtValue = options[i].text || options[i].innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                options[i].classList.remove('highlight');
            } else {
                options[i].classList.add('highlight');
            }
        }
    } else {
        // Очистить стили, если введено менее 3 букв
        var options = input.getElementsByTagName('option');
        for (var i = 0; i < options.length; i++) {
            options[i].classList.remove('highlight');
        }
    }

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
                    "data": "date"
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderEditBtn
                },
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderDeleteBtn
                }
                ,
                {
                    "orderable": false,
                    "defaultContent": "",
                    "render": renderCopyBtn
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

    $.get(getVotingTimeUrl, function (data) {
        $('#time').val(data);
    });
    var date_format = (dateFormat === "ru_date_format" ? 'd.m.Y' : 'Y-m-d');
    $.datetimepicker.setLocale(localeCode);
    $('#date').datetimepicker({
        timepicker: false,
        format: date_format,
        value: new Date().toISOString().substring(0, 10)
    });
    getTimeZones();
    setCurrentTime();
});