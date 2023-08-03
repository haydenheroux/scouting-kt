<#-- @ftlvariable name="events" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.event.Event>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <table border=1>
        <thead>
            <tr>
                <th>Name</th>
                <th>Region</th>
                <th>Year</th>
                <th>Week</th>
            </tr>
        </thead>
        <#list events as event>
        <tr>
            <td>${event.name}</td>
            <td>${event.region}</td>
            <td>${event.year?c}</td>
            <td>${event.week?c}</td>
        </tr>
        </#list>
    </table>
</@layout.header>
