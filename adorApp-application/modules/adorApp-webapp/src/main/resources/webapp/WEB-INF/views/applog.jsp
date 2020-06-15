<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<META HTTP-EQUIV="CACHE-CONTROL" CONTENT="NO-CACHE">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=3.0, user-scalable=yes" />
<meta name="HandheldFriendly" content="true" />
<meta name="apple-mobile-web-app-capable" content="YES" />
<meta name="author" content="Tamas Kohegyi" />
<meta name="Description" content="Perpetual adoration in Hungary, Vác / Örökimádás a váci Szent Anna Piarista Templomban" />
<meta name="Keywords" content="örökimádás,vác,perpetual,adoration" />
<script src="/resources/js/external/jquery-3.4.1.js"></script>
<script src="/resources/js/external/bootstrap-4.3.1.min.js"></script>
<script src="/resources/js/external/dataTables/datatables.min.js"></script>
<script src="/resources/js/common.js"></script>
<script src="/resources/js/applog.js" nonce></script>
<title>Örökimádás - Vác - Adminisztráció</title>
<link href="/resources/css/bootstrap-4.3.1.min.css" rel="stylesheet" media="screen">
<link href="/resources/js/external/dataTables/datatables.min.css" rel="stylesheet" type="text/css"/>
<link href="/resources/css/menu.css" rel="stylesheet" media="screen">
<link id="favicon" rel="shortcut icon" type="image/png" href="/resources/img/favicon.png" />
</head>
<body class="body">
  <div class="container">
    <%@include file="../include/navbar.html" %>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <fieldset class="form-horizontal">
        <legend>Adorálók listája</legend>
        <div class="container" style="padding:5px" align="right"><button id="add-button" type="button" class="btn btn-primary" data-toggle="modal" data-target="#editModal" onclick="addClick()">Új adoráló felvétele...</button></div>
        <div class="container" style="padding:5px" align="right"><button id="refreshAll-button" type="button" class="btn btn-secondary" onclick="processEntityUpdated()">Frissítés</button></div>
        <div class="control-group">
            <table id="person" class="table table-striped table-bordered table-hover compact cell-border" style="width:100%">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Név</th>
                            <th>Adoráló Státusz</th>
                            <th>Web Státusz</th>
                            <th>Telefonszám</th>
                            <th>Telefonszám látható?</th>
                            <th>e-mail</th>
                            <th>e-mail látható?</th>
                            <th>Adminisztrátor megjegyzés</th>
                            <th>Adatkezelési Hozzájárulás</th>
                            <th>Adatk. hozzájárulás dátuma</th>
                            <th>Koordinátor megjegyzés</th>
                            <th>Közös/Látható megjegyzés</th>
                            <th>Nyelvkód</th>
                        </tr>
                    </thead>
                    <tfoot>
                        <tr>
                            <th>ID</th>
                            <th>Név</th>
                            <th>Adoráló Státusz</th>
                            <th>Web Státusz</th>
                            <th>Telefonszám</th>
                            <th>Telefonszám látható?</th>
                            <th>e-mail</th>
                            <th>e-mail látható?</th>
                            <th>Adminisztrátor megjegyzés</th>
                            <th>Adatkezelési Hozzájárulás</th>
                            <th>Adatk. hozzájárulás dátuma</th>
                            <th>Koordinátor megjegyzés</th>
                            <th>Közös/Látható megjegyzés</th>
                            <th>Nyelvkód</th>
                        </tr>
                    </tfoot>
                </table>
            </div>
    </fieldset>


    <fieldset class="form-horizontal">
        <legend>Log Files</legend>
        <div class="control-group">
            <span class="help-block">Click on a log file to download its contents.</span>
        </div>
        <div class="control-group">
            <ol id="div-log-files"></ol>
        </div>
    </fieldset>
  </div>

    <!-- Modal Edit -->
    <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="editCenterTitle">Módosítás</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <form>
                  <table id="editTable" class="table table-hover table-bordered">
                      <thead>
                          <tr>
                              <th>Oszlop név</th>
                              <th style="width:40%">Tartalom</th>
                              <th>Segítség</th>
                          </tr>
                      </thead>
                      <tbody id="editContent"/>
                  </table>
                  <input id="editId" type="hidden" value="">
               </form>
          </div>
          <div class="modal-footer">
            <button id="resetChangesButton" type="button" class="btn btn-secondary" onclick="reBuildModal()">Eredeti adatok visszanyerése</button>
            <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
            <button id="saveChangesButton" type="button" class="btn btn-success" onclick="saveChanges()">Mentés</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Log/History -->
    <div class="modal fade" id="historyModal" tabindex="-1" role="dialog" aria-labelledby="historyCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="historyCenterTitle">Log</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <form>
                  <table id="historyTable" class="table table-hover table-bordered">
                      <thead>
                          <tr>
                              <th>Típus</th>
                              <th>Időpont</th>
                              <th>Végrehajtó</th>
                              <th style="width:40%">Leírás</th>
                              <th>Egyéb</th>
                          </tr>
                      </thead>
                      <tbody id="historyContent"/>
                  </table>
               </form>
          </div>
          <div class="modal-footer">
            <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Time -->
    <div class="modal fade" id="timeModal" tabindex="-1" role="dialog" aria-labelledby="timeCenterTitle" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-xl" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="timeCenterTitle">Órák</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Cancel">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
              <form>
                  <table id="timeTable" class="table table-hover table-bordered">
                      <thead>
                          <tr>
                              <th>Nap</th>
                              <th>Óra</th>
                              <th>Prioritás</th>
                              <th>Online</th>
                              <th>Más adorálók</th>
                              <th>Admin megjegyzés</th>
                              <th>Publikus megjegyzés</th>
                          </tr>
                      </thead>
                      <tbody id="timeContent"/>
                  </table>
              </form>
              <div>&nbsp;<div/>
              <form id="newTimeTable">
                  <input id="editHourPersonId" type="hidden" value="">
                  <input id="editHourId" type="hidden" value="">
                  <table class="table table-hover table-bordered">
                      <thead>
                          <tr>
                              <th>Mező</th>
                              <th>Érték</th>
                          </tr>
                      </thead>
                      <tbody id="newTimeContent">
                          <tr><td>Nap</td><td>
                            <select id="newDay">
                                <option value="0">vasárnap</option>
                                <option value="24">hétfő</option>
                                <option value="48">kedd</option>
                                <option value="72">szerda</option>
                                <option value="96">csütörtök</option>
                                <option value="120">péntek</option>
                                <option value="144">szombat</option>
                            </select></td></tr>
                          <tr><td>Óra</td><td>
                              <select id="newHour">
                                <option value="0">0</option>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                                <option value="6">6</option>
                                <option value="7">7</option>
                                <option value="8">8</option>
                                <option value="9">9</option>
                                <option value="10">10</option>
                                <option value="11">11</option>
                                <option value="12">12</option>
                                <option value="13">13</option>
                                <option value="14">14</option>
                                <option value="15">15</option>
                                <option value="16">16</option>
                                <option value="17">17</option>
                                <option value="18">18</option>
                                <option value="19">19</option>
                                <option value="20">20</option>
                                <option value="21">21</option>
                                <option value="22">22</option>
                                <option value="23">23</option>
                              </select></td></tr>
                          <tr><td>Online</td><td><input type="checkbox" id="newOnline" value=""></td></tr>
                          <tr><td>Prioritás (1..)</td><td>
                            <input type="number" id="newPriority" min="1" max="25">
                          </td></tr>
                          <tr><td>Admin megjegyzés</td><td>
                            <input type="text" id="newAdminComment">
                          </td></tr>
                          <tr><td>Publikus megjegyzés</td><td>
                            <input type="text" id="newPublicComment">
                          </td></tr>
                      </tbody>
                  </table>
              <button type="button" class="btn btn-info" onclick="cancelNewPartOfModal()">Mégsem</button>
              <button id="saveChangesButton" type="button" class="btn btn-success" onclick="saveNewHour()">Mentés</button>
              </form>
          </div>
          <div class="modal-footer">
            <button id="newButton" type="button" class="btn btn-success" onclick="showNewPartOfModal()">Új óra hozzáadása</button>
            <button id="cancelButton" type="button" class="btn btn-info" data-dismiss="modal">Mégsem</button>
          </div>
        </div>
      </div>
    </div>

</body>
</html>
