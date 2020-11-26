<script>
export default {
  created() {
    const { params, query } = this.$route;
    const { path } = params;
    console.log(this.$route);
    this.$router.replace({ path: `/${path}`, query });
  },
  render(h) {
    return h(); // avoid warning message
  }
};
</script>