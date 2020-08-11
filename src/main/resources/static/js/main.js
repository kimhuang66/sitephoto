Vue.component("photo", {
  props: {
    photos: {
      type: Object,
      required: true,
    },
  },
  template: `
        <div class="photo-container">
          <photo-title :title="title"></photo-title>
          <div style="float: right;">
              <a href="#" onclick="print(); return false;">
                  <img src="../images/printer.png">
              </a>
              <a href="#" onclick="print(); return false;">Print
              </a>
          </div>
          <div class="spacer"></div>
          <div class="FERPA-note">
              <p class="FERPA-note">
              Photos reflect current enrollment (no auditors)<br />
              Under Federal Law, they are confidential and only available to instructors and instructional staff.<br />
              They may not be disclosed to third parties without written permission from the student.
              </p>
          </div>
          <div class="spacer"></div>
          <div class="misc-parent">
            <div class="view-search-section">
                  <groupselection v-if="hasGroups" :groups="groups" :selected_proxy="selected_proxy" @show-diff-group="showDiffGroup($event)"></groupselection>
            </div>
              <div class="spacer"></div>
              <div class="spacer"></div>
              <div class="numberOfS">
                <numberOfS :students="students" :selected_proxy="selected_proxy" :searched_proxy="searched_proxy"  ></numberOfS>
              </div>
              <div class="spacer"></div>
              <div class="spacer"></div>
              <searchwidget :selected_proxy="selected_proxy" :searched_proxy="searched_proxy" @show-search-result="showSearchResult($event)"></searchwidget>
          </div>
          <div class="student-data">
              <ul class="student-section">
                  <li style="list-style: none;" v-for="student in students" ><student :student="student" :selected_proxy="selected_proxy" :searched_proxy="searched_proxy"></student></li>
              </ul>
          </div>
      </div>
      `,
  data() {
    return {
      crs_id: this.photos.crs_id,
      crs_name: this.photos.crs_name,
      groups: this.photos.groups,
      students: this.photos.students,
      selected_proxy: "All available",
      searched_proxy: "",
    };
  },
  methods: {
    showDiffGroup: function (newvalue) {
      this.selected_proxy = newvalue;
    },
    showSearchResult: function (newvalue) {
      this.searched_proxy = newvalue;
    },
  },

  computed: {
    title() {
      if (this.hasGroups) {
        return "Photos with groups: " + this.crs_name;
      } else {
        return "Photos: " + this.crs_name;
      }
    },
    hasGroups() {
      return this.groups.length;
    },
  },
});
Vue.component("photo-title", {
  props: {
    title: {
      type: String,
      required: true,
    },
  },
  template: `
          <div class="photo-title">{{ title }}</div>
      `,
});
Vue.component("groupselection", {
  props: {
    groups: {
      type: Array,
      required: false,
    },
    selected_proxy: {
      type: String,
      required: true,
    },
  },
  template: `
      <div class="groupselection">
          view : <select v-model="myselect"  @change="onchange">
              <option value="All available">All sections</option>
              <option value="stu_search">Search on student's name</option>
              <option v-for="group in groups" :key=group.id>{{ group.meeting }}</option>
               
          </select>
      </div>
      `,
  data() {
    return {
      myselect: this.selected_proxy,
    };
  },
  methods: {
    onchange: function () {
      this.$emit("show-diff-group", this.myselect);
    },
  },
});
Vue.component("numberOfS", {
  template: `
    <div class="memberInfo-description" >
        {{numberofStudent}} students
     </div>
     `,
  props: {
    students: {
      type: Array,
      required: true,
    },
    selected_proxy: {
      type: String,
      required: true,
    },
    searched_proxy: {
      type: String,
      required: true,
    },
  },
  computed: {
    numberofStudent() {
      if (this.selected_proxy === "All available") {
        return this.students === undefined ? 0 : this.students.length;
      } else if (this.selected_proxy === "stu_search") {
        let sp =
          this.searched_proxy === "" ? "" : this.searched_proxy.toUpperCase();
        if (sp === "") {
          return 0;
        }
        const ss = this.students;
        let n = 0;
        ss.forEach((i) => {
          const s = JSON.parse(i);
          if (s.prefer_name.toUpperCase().includes(sp)) {
            n++;
          }
        });
        return n;
      } else {
        const ss = this.students;
        const selected = this.selected_proxy;
        var n = 0;
        ss.forEach(function (i, index) {
          const s = JSON.parse(i);
          for (let j = 0; j < s.precept.length; j++) {
            let pre = s.precept[j];
            let a = pre.meeting.replace(/\s/g, "");
            let b = selected.replace(/\s/g, "");
            if (a.localeCompare(b) == 0) {
              n++;
              break;
            }
          }
        });
        return n;
      }
    },
  },
});
Vue.component("searchwidget", {
  props: {
    searched_proxy: {
      type: String,
      required: true,
    },
    selected_proxy: {
      type: String,
      required: true,
    },
  },
  template: `
      <div class="searchwidget" :style="{display: issearchvisible }">
          <input type="text" v-model="searched" v-on:keyup.enter="oninput" ref="searchinput" placeholder="First or Last name" ></input>
         <!-- <button v-on:click="oninput">find</button> -->
          <button v-on:click="onclear">clear</button>
      </div>
    `,
  data() {
    return { searched: this.searched_proxy };
  },
  computed: {
    issearchvisible() {
      if (this.selected_proxy == "stu_search") {
        return "block";
      }
      return "none";
    },
  },
  methods: {
    oninput: function () {
      this.$emit("show-search-result", this.searched);
    },
    onclear: function () {
      this.searched = "";
      this.$emit("show-search-result", "");
      this.$refs["searchinput"].value = "";
    },
  },
});
Vue.component("student", {
  template: `
      <div class="student-item" :style="{display: isvisible }">
          <div class="student-image" ref="student-image">
              <img class="student-image" v-if="hasImg" :src="imgstring" 
              alt="student-image" :data-large="imgstring"
              data-pin-no-hover="true"></img>
              <img class="student-image" v-else src="../images/default_photo.png" alt="student-image"></img>
          </div>
          <div class="student-info">
              <p class="student-item-name">{{ preferName }}</p>
              <p class="student-item" :style="{display: isSecVisible }">{{ precept }}</p>
              <p class="student-item" v-if="hasMajor">{{ major }}</p>
            <!--  <p class="student-item">Class: {{ year }}</p> -->
            <!--  <p class="student-item" v-if="isUG">Class: {{ year }}</p> -->
            <!--  <p class="student-item" v-if="hasRes"><span style="font-weight: bold">College: </span>{{ residentialCollege }}</p> -->
            <p class="student-item" v-if="hasRes">{{ residentialCollege }} College</p>
            <p class="student-item" >{{ email }}</p>
            <!-- <a class="student-item" style="color:blue;" :href="'mailto:' + email">{{ email }}</a> -->
            <!--  <p class="student-item"><span style="font-weight: bold">PUID: </span>{{ puid }}</p> -->
          </div>
      </div>
      `,
  props: {
    student: {
      type: String,
      required: true,
    },
    selected_proxy: {
      type: String,
      required: true,
    },
    searched_proxy: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      searched: this.searched_proxy,
    };
  },
  computed: {
    isvisible() {
      //console.log(this.computedMeeting.indexOf(this.selected_proxy));
      var s = false;
      var display = "";
      let sp = "";
      if (this.selected_proxy === "stu_search") {
        sp =
          this.searched_proxy === "" ? "" : this.searched_proxy.toUpperCase();
      }
      if (
        this.selected_proxy === "stu_search" &&
        sp != "" &&
        this.item.prefer_name.toUpperCase().includes(sp)
      ) {
        return "block";
      }
      if (this.selected_proxy === "All available") {
        return "block";
      }
      if (
        this.selected_proxy != "stu_search" &&
        this.computedMeeting != undefined
      ) {
        for (let j = 0; j < this.computedMeeting.length; j++) {
          let pre = this.computedMeeting[j];
          let a = pre.replace(/\s/g, "");
          let b = this.selected_proxy.replace(/\s/g, "");
          //console.log("a: " + a);
          //console.log("b: " + b);
          if (a.localeCompare(b) == 0) {
            return "block";
          }
        }
      }
      if (
        this.selected_proxy != "stu_search" &&
        this.computedMeeting == undefined
      ) {
        return "none";
        // return "block";
      }
      return "none";
    },
    isSecVisible() {
      return this.selected_proxy === "stu_search" ? "block" : "none";
    },
    precept() {
      if (this.item.precept != null && this.item.precept.length > 0) {
        let p = this.item.precept[0].id;
        for (let i = 1; i < this.item.precept.length; i++) {
          p = p + "," + this.item.precept[i].id;
        }
        return p;
      } else {
        return "";
      }
    },
    item() {
      return JSON.parse(this.student);
    },
    preferName() {
      return this.item.prefer_name;
    },
    puid() {
      return this.item.puid;
    },
    residentialCollege() {
      return this.item.res_college;
    },
    major() {
      return this.item.major;
    },
    // year() {
    //   return this.item.class_year;
    // },
    hasImg() {
      if (this.item.photo != undefined || this.item.photo.length > 0) {
        return true;
      }
      return false;
    },
    email() {
      return this.item.netid + "@princeton.edu";
    },
    computedMeeting() {
      var m = new Array();
      if (this.item.precept != undefined) {
        this.item.precept.forEach((e) => {
          m.push(e.meeting);
        });
        return m;
      }
      return null;
    },
    isUG() {
      return this.item.major || !this.item.major === "";
    },
    hasMajor() {
      return this.item.major || !this.item.major === "";
    },
    hasRes() {
      if (
        this.item.res_college != undefined &&
        this.item.res_college.length > 0
      ) {
        return true;
      }
      return false;
    },
    imgstring() {
      if (this.hasImg) {
        return "data:image/png;base64, " + this.item.photo;
      } else {
        return "";
      }
    },
  },
});

var app = new Vue({
  el: "#app",
  template: `
    <section v-if="errored">
        <p>We're sorry, we're not able to retrieve this information at the moment, please try back later.</p>
    </section>
    <section v-else>
        <div v-if="loading">Loading...</div>
        <div v-else>
            <photo :photos="photos"></photo>
        </div>
    </section>
  `,
  data() {
    return {
      coursemembers: {},
      loading: true,
      errored: false,
    };
  },
  computed: {
    photos() {
      return {
        crs_id: this.crs_id,
        crs_name: this.crs_name,
        students: this.coursemembers.students,
        groups:
          this.coursemembers.groups === undefined
            ? []
            : this.coursemembers.groups,
      };
    },
  },
  beforeMount: function () {
    this.crs_id = this.$el.attributes["crs_id"].value;
    this.crs_name = this.$el.attributes["crs_name"].value;
  },
  mounted: function () {
    axios
      .get("../images?crs_id=" + this.crs_id)
      //   .then(function(response) {
      //     console.log(response);
      //   })
      .then((response) => (this.coursemembers = response.data))
      .catch((error) => (this.errored = true))
      .finally(() => (this.loading = false));
  },
});
