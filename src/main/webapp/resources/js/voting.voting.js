const restaurantAjaxUrl = "voting";
const getVotingTimeUrl = "voting/voting_time";
const getResultUrl = "profile/result";
const my_votes = "voting/my_votes";
let voted;
let myVotes

function vote(restaurant_id) {
    $.post({
        url: restaurantAjaxUrl,
        data: {restaurant_id: restaurant_id},
        dataType: "json"
    }).done(function (data) {
        if (voted !== data.restaurant.id && voted !== undefined) {
            $("#" + voted).css('background-color', '#fff');
        }
        voted = data.restaurant.id;
        $("#" + voted).css('background-color', '#1b47d7');
        successNoty("common.restaurant", "common.voted");
    });
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function failNoty(jqXHR) {
    closeNoty();
    var errorInfo = jqXHR.responseJSON;
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + errorInfo.message.join("<br>"),
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show()
}

function getResult() {
    $.get(getResultUrl, function (data) {
        $('#dishes>li').detach();
        $('#restaurant_name').html(data.name);
        $('#count').html(data.countVotes)
        $.each(data.dishes, function (index, dish) {
            $('#dishes').append($('<li>', {html: dish.name + ' ' + dish.price}));
        });
    }).done(function () {
        $('#result').modal();
    });
}

function getMyVotes() {
    myVotes.ajax.reload();
    $('#votes').modal();
}

function successNoty(key, value) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + i18n[key]
            + " " + $("#" + voted + ">h3").html() + " " + i18n[value],
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function getVotingTime() {
    $.get(getVotingTimeUrl, function (data) {
        let leftMinutes = getLeftMinutes(data);
        let number = checkTime(leftMinutes);
        let interval = setInterval(function () {
            if (number === 0) {
                clearInterval(interval);
            } else {
                number = checkTime(number);
            }
        }, 1000);
    });
}

function getLeftMinutes(data) {
    let leftMinutes;
    let $time = data.split(":");
    var voting_end_hour = Number($time[0]);
    var voting_end_minute = Number($time[1]);
    var curent_time = new Date();
    var current_minutes = curent_time.getHours() * 60 + curent_time.getMinutes();
    var end_voting_minutes = voting_end_hour * 60 + voting_end_minute;
    leftMinutes = end_voting_minutes - current_minutes;
    if (leftMinutes < 0) {
        leftMinutes = 0;
    }
    return leftMinutes;
}

function getLeftMinutesAndHours(minutes) {
    var hours = Math.floor(minutes / 60);
    minutes -= (hours * 60);
    if (hours < 10) {
        hours = '0' + hours;
    }
    if (minutes < 10) {
        minutes = '0' + minutes;
    }
    return (hours + ':' + minutes);
}

function checkTime(minutes) {
    if (minutes === 0) {
        renderRestultButton();
        $('#background').css('background-color', '#dc1038');
    } else {
        minutes -= 1;
    }
    $('#votingTime').html(getLeftMinutesAndHours(minutes));
    return minutes;
}

function renderRestultButton() {
    $('#votebutton').append($('<button>', {
        class: "btn btn-primary mybutton",
        onclick: 'getResult()'
    }))
    $('#votebutton>button').html(i18n["voting.result"]);
}

$(function () {
    getVotingTime();
    $.ajax({
        url: restaurantAjaxUrl,
        method: "GET",
        dataType: "json"
    }).done(function (data) {
        voted = data.restaurant.id;
        if (voted) {
            $("#" + voted).css('background-color', '#1b47d7');
        }
    });

    myVotes = $("#my_votes").DataTable({
        "columns": [
            {
                "data": "restaurant.name"
            },
            {
                "data": "date",
            },
        ],
        "ajax": {
            "url": my_votes,
            "dataSrc": ""
        },
        "order": [
            [
                1,
                "desc"
            ]
        ]
    });

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});