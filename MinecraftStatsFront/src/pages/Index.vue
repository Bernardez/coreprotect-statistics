<template>
  <div class="page">
    <q-table
      :data="tableData"
      :columns="columns"
      :visible-columns="visibleColumns"
      row-key="name"
      :pagination.sync="pagination"
      separator="cell"
    >
      <template slot="top-left" slot-scope="props">
        <q-table-columns
          color="secondary"
          class="q-mr-sm"
          v-model="visibleColumns"
          :columns="columns"
        >Test</q-table-columns>
      </template>
    </q-table>
  </div>
</template>

<script>
export default {
  name: 'Index',
  data: () => ({
    columns: [],
    tableData: [],
    visibleColumns: ['user', 'diamond ore', 'iron ore', 'coal ore', 'emerald ore', 'redstone ore', 'stone', 'dirt'],
    pagination: {
      sortBy: null, // String, column "name" property value
      descending: false,
      page: 1,
      rowsPerPage: 15 // current rows per page being displayed
    }
  }),
  mounted () {
    this.fetchData()
  },
  methods: {
    fetchData () {
      this.$axios.get('>>BACKEND-HTTP-URL<<')
        .then((response) => {
          this.tableData = response.data
          // console.log(this.tableData)
          let columns = []
          let visisbleColumns = []
          for (let key in response.data[0]) {
            columns.push(
              {
                name: key,
                label: key,
                field: key,
                align: 'left',
                sortable: true
              }
            )
            visisbleColumns.push(key)
          }
          this.columns = columns
          // this.visibleColumns = visisbleColumns
        })
        .catch(() => {
          this.$q.notify({
            color: 'negative',
            position: 'top',
            message: 'Loading failed',
            icon: 'report_problem'
          })
        })
    }
  }
}
</script>

<style>
  body {
    background-image: url("../statics/woodbg.png");
  }
  .page {
    background-color: #ffffff;
    opacity: 0.9;
    margin: 100px;
  }
</style>
