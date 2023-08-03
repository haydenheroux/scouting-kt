<#-- @ftlvariable name="teams" type="kotlin.collections.List<io.github.haydenheroux.scouting.models.team.Team>" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <table border=1>
        <tr>
            <th>Number</th>
            <th>Name</th>
            <th>Region</th>
        </tr>
        <#list teams as team>
        <tr>
            <td><a href="/teams/${team.number?c}">${team.number?c}</a></td>
            <td>${team.name}</td>
            <td>${team.region}</td>
        </tr>
        </#list>
    </table>
</@layout.header>
